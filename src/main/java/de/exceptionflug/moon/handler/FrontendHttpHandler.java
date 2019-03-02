package de.exceptionflug.moon.handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import de.exceptionflug.moon.Cookies;
import de.exceptionflug.moon.MultiPartFormData;
import de.exceptionflug.moon.Request;
import de.exceptionflug.moon.WebApplication;
import de.exceptionflug.moon.response.AbstractResponse;
import de.exceptionflug.moon.response.DirectoryResponse;
import de.exceptionflug.moon.response.InternalServerErrorResponse;
import de.exceptionflug.moon.response.NotFoundResponse;
import org.apache.http.client.utils.URIBuilder;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FrontendHttpHandler extends FormDataHandler {

    private static final Logger LOGGER = Logger.getLogger(FrontendHttpHandler.class.getName());

    private final WebApplication webApplication;

    public FrontendHttpHandler(final WebApplication webApplication) {
        this.webApplication = webApplication;
    }

    @Override
    public void handle(final HttpExchange httpExchange, final List<MultiPart> parts) throws IOException {
        final Headers headers = httpExchange.getRequestHeaders();
        final Cookies cookies = new Cookies(headers.get("Cookie"));
        final URI requestURI = httpExchange.getRequestURI();
        AbstractResponse response = webApplication.getResponse(requestURI);
        if(response instanceof DirectoryResponse) {
            try {
                final URI build = new URIBuilder(requestURI).setPath(requestURI.getPath() + "/index.html").build();
                final AbstractResponse index = webApplication.getResponse(build);
                if(!(index instanceof NotFoundResponse)) {
                    new Request(httpExchange, cookies, null).rewriteLocation(build.toASCIIString());
                    return;
                }
            } catch (final URISyntaxException e) {
            } // Won't happen
        }
        try {
            final PageHandler pageHandler = webApplication.getPageHandler(requestURI);
            final AbstractResponse out = pageHandler.handle(response, new Request(httpExchange, cookies, parts != null ? new MultiPartFormData(parts) : null));
            if(out != null)
                response = out;
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Web error", e);
            response = new InternalServerErrorResponse(e);
        }
        if(response.isCancelled())
            return;
        if(response.getContentType() != null) {
            httpExchange.getResponseHeaders().add("Content-Type", response.getContentType());
        }
        if(cookies.size() > 0) {
            cookies.setCookies(httpExchange.getResponseHeaders());
        }
        httpExchange.sendResponseHeaders(response.getStatusCode(), response.getData().length);
        try {
            try(final OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getData());
                os.flush();
            }
        } catch (final IOException e) {
            // Ignored
        }
    }

}
