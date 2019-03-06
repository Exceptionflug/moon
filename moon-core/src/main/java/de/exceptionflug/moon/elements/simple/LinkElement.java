package de.exceptionflug.moon.elements.simple;

import de.exceptionflug.moon.DomElement;

public class LinkElement extends DomElement {

    private final DomElement body;
    private final String href;

    public LinkElement(final DomElement body, final String href) {
        this.body = body;
        this.href = href;
    }

    @Override
    public String toString() {
        return "<a href=\""+href+"\">"+body+"</a>";
    }
}
