package de.exceptionflug.moon.handler;

import de.exceptionflug.moon.Request;
import de.exceptionflug.moon.response.AbstractResponse;

import java.util.concurrent.atomic.AtomicReference;

public interface PageHandler<T extends AbstractResponse> {

    AbstractResponse handle(final T response, final Request request) throws Exception;

}
