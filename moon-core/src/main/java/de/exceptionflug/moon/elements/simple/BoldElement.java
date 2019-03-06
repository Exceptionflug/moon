package de.exceptionflug.moon.elements.simple;

import de.exceptionflug.moon.DomElement;

public class BoldElement extends DomElement {

    private final DomElement domElement;

    public BoldElement(final DomElement domElement) {
        this.domElement = domElement;
    }

    @Override
    public String toString() {
        return "<b>"+domElement+"</b>";
    }

}
