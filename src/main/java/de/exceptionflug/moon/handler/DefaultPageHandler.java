package de.exceptionflug.moon.handler;

import de.exceptionflug.moon.Request;
import de.exceptionflug.moon.response.AbstractResponse;

public class DefaultPageHandler implements PageHandler {

    @Override
    public AbstractResponse handle(final AbstractResponse response, final Request request) {
        return response;
    }

}
