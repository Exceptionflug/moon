package de.exceptionflug.moon;

import com.sun.net.httpserver.HttpExchange;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Request {

    private final HttpExchange httpExchange;
    private final Cookies cookies;
    private final MultiPartFormData multiPartFormData;

    public Request(final HttpExchange httpExchange, final Cookies cookies, final MultiPartFormData multiPartFormData) {
        this.httpExchange = httpExchange;
        this.cookies = cookies;
        this.multiPartFormData = multiPartFormData;
    }

    public void rewriteLocation(String target) throws IOException {
        rewriteLoc(target);
        httpExchange.sendResponseHeaders(302, 0);
        httpExchange.close();
    }

    public void rewriteLocationAndQuery(String target, final String query) throws Exception {
        if (target.startsWith("/")) {
            final URI uri = new URIBuilder(httpExchange.getRequestURI()).setPath(target).setCustomQuery(query).build();
            target = uri.toASCIIString();
        } else {
            target = new URIBuilder(target).setCustomQuery(query).build().toASCIIString();
        }
        httpExchange.getResponseHeaders().add("Location", target);
        httpExchange.sendResponseHeaders(302, 0);
        httpExchange.close();
    }

    public void rewriteLocationAndWriteCookies(String target, final Cookies cookies) throws IOException {
        rewriteLoc(target);
        cookies.setCookies(httpExchange.getResponseHeaders());
        httpExchange.sendResponseHeaders(302, 0);
        httpExchange.close();
    }

    private void rewriteLoc(String target) {
        if (target.startsWith("/")) {
            try {
                final URI uri = new URIBuilder(httpExchange.getRequestURI()).setPath(target).build();
                target = uri.toASCIIString();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        httpExchange.getResponseHeaders().add("Location", target);
    }

    public HttpExchange getHttpExchange() {
        return httpExchange;
    }

    public Cookies getCookies() {
        return cookies;
    }

    public MultiPartFormData getMultiPartFormData() {
        return multiPartFormData;
    }

}
