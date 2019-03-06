package de.exceptionflug.moon.response;

import de.exceptionflug.moon.elements.simple.MoonFooterElement;

import java.net.URI;

public class NotFoundResponse extends TextResponse {

    public NotFoundResponse(final URI requested) {
        super("<html><body><h1>Not Found!</h1><p>No context available for path "+requested.getPath()+"</p>"+new MoonFooterElement() +"</body></html>", "text/html", 404);
    }

}
