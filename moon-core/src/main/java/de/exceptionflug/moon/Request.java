package de.exceptionflug.moon;

import com.sun.net.httpserver.HttpExchange;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class Request {

    private final HttpExchange httpExchange;
    private final Cookies cookies;
    private final MultiPartFormData multiPartFormData;
    private final Map<String, String> queryParameters;

    public Request(final HttpExchange httpExchange, final Cookies cookies, final MultiPartFormData multiPartFormData) {
        this.httpExchange = httpExchange;
        this.cookies = cookies;
        this.multiPartFormData = multiPartFormData;
        this.queryParameters = parseQueryString(httpExchange.getRequestURI().getQuery());
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

    private Map<String, String> parseQueryString(String qs) {
        Map<String, String> result = new HashMap<>();
        if (qs == null)
            return result;

        int last = 0, next, l = qs.length();
        while (last < l) {
            next = qs.indexOf('&', last);
            if (next == -1)
                next = l;

            if (next > last) {
                int eqPos = qs.indexOf('=', last);
                try {
                    if (eqPos < 0 || eqPos > next)
                        result.put(URLDecoder.decode(qs.substring(last, next), "utf-8"), "");
                    else
                        result.put(URLDecoder.decode(qs.substring(last, eqPos), "utf-8"), URLDecoder.decode(qs.substring(eqPos + 1, next), "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
            last = next + 1;
        }
        return result;
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

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    public String getQueryParameter(String name) {
        return queryParameters.get(name);
    }

}
