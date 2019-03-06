package de.exceptionflug.moon.response;

import de.exceptionflug.moon.elements.simple.MoonFooterElement;
import de.exceptionflug.moon.utils.WebExceptionFormatter;

public class InternalServerErrorResponse extends TextResponse {

    public InternalServerErrorResponse(final Exception e) {
        super("<html><body><h1>Internal Server Error</h1>::%exception%::"+new MoonFooterElement() +"</body></html>", "text/html", 500);
        if(e != null) {
            replace("::%exception%::", WebExceptionFormatter.format(e));
        }
    }

}
