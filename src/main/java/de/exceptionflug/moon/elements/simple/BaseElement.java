package de.exceptionflug.moon.elements.simple;

import de.exceptionflug.moon.DomElement;

public class BaseElement extends DomElement {

    private final String text;

    public BaseElement(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

}
