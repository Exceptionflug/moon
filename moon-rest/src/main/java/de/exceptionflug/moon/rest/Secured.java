package de.exceptionflug.moon.rest;

import de.exceptionflug.moon.rest.auth.Authenticator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Secured {

    Class<? extends Authenticator> value();

}
