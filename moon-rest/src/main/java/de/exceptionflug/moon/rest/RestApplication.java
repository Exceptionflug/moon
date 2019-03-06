package de.exceptionflug.moon.rest;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Streams;
import com.sun.net.httpserver.HttpServer;
import de.exceptionflug.moon.WebApplication;
import de.exceptionflug.moon.response.AbstractResponse;
import de.exceptionflug.moon.response.ForbiddenResponse;
import de.exceptionflug.moon.rest.auth.Authenticator;

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
    }

    public void registerRestController(final Object object) {
        final Class<?> clazz = object.getClass();
        if(!clazz.isAnnotationPresent(RestController.class))
            throw new IllegalArgumentException("The class "+ clazz.getName()+" is not describing a rest controller.");
        final RestController controller = clazz.getAnnotation(RestController.class);
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
                registerPageHandler(mapping.path(), (response, request) -> {
                    if(authenticator.get() != null) {
                        if(!authenticator.get().authenticate(request))
                            return new ForbiddenResponse(request.getHttpExchange().getRequestURI());
                    }
                    final Map<String, String> parameters;
                    if(mapping.method() == HttpMethod.GET) {
                         parameters = queryToMap(request.getHttpExchange().getRequestURI().getQuery());
                    } else {
                        parameters = new HashMap<>();
                    }
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

                            if(mapping.method() == HttpMethod.GET) {

                            } else {

                            }
                        }
                    }
                    return response;
                });
            }
        }
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    private Map<String, String> queryToMap(final String query) {
        final Map<String, String> result = new HashMap<>();
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

    public static WebApplication quickStart(final InetSocketAddress address, final int backlog, final String rootPath) throws IOException {
        final HttpServer server = HttpServer.create(address, backlog);
        server.start();
        return new RestApplication(server, rootPath);
    }

}
