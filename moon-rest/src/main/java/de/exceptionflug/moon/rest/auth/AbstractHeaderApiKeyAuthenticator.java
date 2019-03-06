package de.exceptionflug.moon.rest.auth;

import com.sun.net.httpserver.Headers;
import de.exceptionflug.moon.Request;

public abstract class AbstractHeaderApiKeyAuthenticator implements Authenticator {

    private final String apiKey;

    protected AbstractHeaderApiKeyAuthenticator(final String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public boolean authenticate(final Request request) {
        final Headers requestHeaders = request.getHttpExchange().getRequestHeaders();
        if(requestHeaders.containsKey("ApiKey")) {
            final String key = requestHeaders.getFirst("ApiKey");
            return key.equals(apiKey);
        }
        return false;
    }
}
