package de.exceptionflug.moon.elements.simple;

import de.exceptionflug.moon.DomElement;
import de.exceptionflug.moon.WebApplication;

public class MoonFooterElement extends DomElement {
    @Override
    public String toString() {
        return "<hr><i>Moon "+ WebApplication.VERSION +" on Sun HttpServer v"+Runtime.class.getPackage().getImplementationVersion()+" running on "+System.getProperty("os.name")+"</i>";
    }
}
