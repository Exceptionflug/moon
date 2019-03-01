package de.exceptionflug.moon.utils;

import de.exceptionflug.moon.DomElement;
import de.exceptionflug.moon.elements.simple.BaseElement;

public final class WebExceptionFormatter {

    private WebExceptionFormatter() {}

    public static DomElement format(final Exception e) {
        final StringBuilder builder = new StringBuilder(e.getClass().getName()+": "+(e.getLocalizedMessage() != null ? e.getLocalizedMessage() : ""));
        for(final StackTraceElement element : e.getStackTrace()) {
            builder.append("<br>&emsp;at ").append(element);
        }
        if(e.getCause() != null)
            addCause(builder, e.getCause());
        return new BaseElement(builder.toString());
    }

    private static void addCause(final StringBuilder builder, final Throwable cause) {
        builder.append("<br>caused by "+cause.getClass().getName()+": "+(cause.getLocalizedMessage() != null ? cause.getLocalizedMessage() : ""));
        for(final StackTraceElement element : cause.getStackTrace()) {
            builder.append("<br>&emsp;at ").append(element);
        }
        if(cause.getCause() != null)
            addCause(builder, cause.getCause());
    }

}
