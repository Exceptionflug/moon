package de.exceptionflug.moon.rest;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpServer;
import de.exceptionflug.moon.WebApplication;
import de.exceptionflug.moon.response.AbstractResponse;
import de.exceptionflug.moon.response.ForbiddenResponse;
import de.exceptionflug.moon.rest.auth.Authenticator;
import de.exceptionflug.moon.rest.response.JsonResponse;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class RestApplication extends WebApplication {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public RestApplication(final HttpServer server, final String rootPath) {
        super(server, rootPath);
        objectMapper.setVisibilityChecker(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
    }

    public void registerRestController(final Object object) {
        final Class<?> clazz = object.getClass();
        if(!clazz.isAnnotationPresent(RestController.class))
            throw new IllegalArgumentException("The class "+ clazz.getName()+" is not describing a rest controller.");
        final AtomicReference<Authenticator> authenticator = new AtomicReference<>();
        if(clazz.isAnnotationPresent(Secured.class)) {
            try {
                authenticator.set(clazz.getAnnotation(Secured.class).value().newInstance());
            } catch (final Exception e) {
                Logger.getLogger(RestApplication.class.getName()).log(Level.SEVERE, "Cannot instantiate authenticator", e);
            }
        }
        for(final Method method : clazz.getMethods()) {
            if(AbstractResponse.class.isAssignableFrom(method.getReturnType()) && method.isAnnotationPresent(Mapping.class)) {
                final Mapping mapping = method.getAnnotation(Mapping.class);
                final String path = mapping.path().startsWith("/") ? mapping.path().substring(1) : mapping.path();
                registerPageHandler(path, (response, request) -> {
                    if(authenticator.get() != null) {
                        if(!authenticator.get().authenticate(request))
                            return new ForbiddenResponse(request.getHttpExchange().getRequestURI());
                    }
                    final Map<String, Object> parameters;
                    if(mapping.method() == HttpMethod.GET) {
                         parameters = queryToMap(request.getHttpExchange().getRequestURI().getQuery());
                    } else {
                        parameters = new HashMap<>();
                        final Scanner scanner = new Scanner(request.getHttpExchange().getRequestBody()).useDelimiter("\\A");
                        String body = scanner.hasNext() ? scanner.next() : "{}";
                        if(body.isEmpty())
                            body = "{}";
                        final ObjectNode node = (ObjectNode) objectMapper.readTree(body);
                        final Iterator<Map.Entry<String, JsonNode>> iterator = node.fields();
                        while (iterator.hasNext()) {
                            final Map.Entry<String, JsonNode> entry = iterator.next();
                            parameters.put(entry.getKey(), entry.getValue());
                        }
                    }
                    final Object[] params = new Object[method.getParameterCount()];
                    if(request.getHttpExchange().getRequestMethod().equalsIgnoreCase(mapping.method().name())) {
                        for (int i = 0; i < method.getParameterTypes().length; i++) {
                            final Class<?> paramType = method.getParameterTypes()[i];
                            final Annotation[] annotations = method.getParameterAnnotations()[i];
                            final NamedParameter namedParameter = (NamedParameter) Stream.of(annotations).filter(it -> it instanceof NamedParameter).findFirst().orElse(null);

                            final String parameterName;
                            final boolean optional;
                            if(namedParameter != null) {
                                parameterName = namedParameter.value();
                                optional = namedParameter.optional();
                            } else {
                                parameterName = paramType.getSimpleName().substring(0, 1).toLowerCase() + paramType.getSimpleName().substring(1);
                                optional = false;
                            }

                            final Object obj = parameters.get(parameterName);
                            if(obj == null && !optional)
                                throw new NullPointerException(parameterName+" is not present [path = "+request.getHttpExchange().getRequestURI().getPath()+"]");
                            if(mapping.method() == HttpMethod.GET) {
                                if(obj != null && paramType.isAssignableFrom(obj.getClass())) {
                                    params[i] = obj;
                                } else if(obj != null) {
                                    final String string = (String) obj;
                                    if(paramType.equals(int.class) || paramType.equals(Integer.class)) {
                                        params[i] = Integer.parseInt(string);
                                    } else if(paramType.equals(double.class) || paramType.equals(Double.class)) {
                                        params[i] = Double.parseDouble(string);
                                    } else if(paramType.equals(float.class) || paramType.equals(Float.class)) {
                                        params[i] = Float.parseFloat(string);
                                    } else if(paramType.equals(short.class) || paramType.equals(Short.class)) {
                                        params[i] = Short.parseShort(string);
                                    } else if(paramType.equals(byte.class) || paramType.equals(Byte.class)) {
                                        params[i] = Byte.parseByte(string);
                                    } else if(paramType.equals(long.class) || paramType.equals(Long.class)) {
                                        params[i] = Long.parseLong(string);
                                    } else if(paramType.equals(UUID.class)) {
                                        params[i] = UUID.fromString(string);
                                    } else throw new IllegalArgumentException("Invalid request parameter "+parameterName+": String, UUID and primitive types are the only allowed data types when using method GET");
                                } else {
                                    params[i] = obj;
                                }
                            } else {
                                params[i] = objectMapper.convertValue(obj, paramType);
                            }
                        }
                    } else {
                        throw new IllegalStateException("Wrong http method. Expected "+mapping.method().name()+" but got "+request.getHttpExchange().getRequestMethod());
                    }
                    final AbstractResponse out = (AbstractResponse) method.invoke(object, params);
                    if(out instanceof JsonResponse)
                        ((JsonResponse) out).serialize(objectMapper);
                    return out;
                });
            }
        }
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    private Map<String, Object> queryToMap(final String query) {
        if(query == null)
            return Collections.emptyMap();
        final Map<String, Object> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], "");
            }
        }
        return result;
    }

    // <-------------------------------->

    public static RestApplication quickStart(final InetSocketAddress address, final int backlog, final String rootPath) throws IOException {
        final HttpServer server = HttpServer.create(address, backlog);
        server.start();
        return new RestApplication(server, rootPath);
    }

}
