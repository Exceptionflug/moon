package de.exceptionflug.moon.rest.auth;

import de.exceptionflug.moon.Request;

public interface Authenticator {

    boolean authenticate(final Request request);

}
