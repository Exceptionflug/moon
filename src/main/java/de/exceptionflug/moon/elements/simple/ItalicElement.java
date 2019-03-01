package de.exceptionflug.moon.elements.simple;

import de.exceptionflug.moon.DomElement;

public class ItalicElement extends DomElement {

    private final DomElement body;

    public ItalicElement(final DomElement body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "<i>"+body+"</i>";
    }
}
