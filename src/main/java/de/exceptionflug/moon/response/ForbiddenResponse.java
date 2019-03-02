package de.exceptionflug.moon.response;

import de.exceptionflug.moon.elements.simple.MoonFooterElement;

import java.net.URI;

public class ForbiddenResponse extends TextResponse {

    public ForbiddenResponse(final URI requested) {
        super("<html><body><h1>Forbidden!</h1><p>Access denied for path "+requested.getPath()+"</p>"+new MoonFooterElement() +"</body></html>", "text/html", 403);
    }

}
