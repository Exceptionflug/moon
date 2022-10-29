package de.exceptionflug.moon;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import de.exceptionflug.moon.handler.DefaultPageHandler;
import de.exceptionflug.moon.handler.FrontendHttpHandler;
import de.exceptionflug.moon.handler.PageHandler;
import de.exceptionflug.moon.response.*;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

public class WebApplication {

    public static final String VERSION = "v1.0.2";

    private final Map<String, PageHandler> pageHandlerMap = new HashMap<>();
    private final Map<String, HttpContext> contextMap = new HashMap<>();
    private final FrontendHttpHandler httpHandler = new FrontendHttpHandler(this);
    private final File htmlRoot;
    private final String rootPath;
    private final HttpServer server;
    private PageHandler defaultPageHandler = new DefaultPageHandler();

    public WebApplication(final HttpServer server, final String rootPath) {
        this(server, rootPath, null);
    }

    public WebApplication(final HttpServer server, final String rootPath, final File htmlRoot) {
        Preconditions.checkNotNull(server, "The server cannot be null");
        Preconditions.checkNotNull(rootPath, "The root path cannot be null");
        this.htmlRoot = htmlRoot;
        this.rootPath = rootPath;
        this.server = server;
        contextMap.put(rootPath, server.createContext(rootPath, httpHandler));
    }

    public void shutdown(final int maxDelay) {
        server.stop(maxDelay);
    }

    public void shutdown() {
        server.stop(10);
    }

    public AbstractResponse getResponse(final URI requestUri) throws IOException {
        if (htmlRoot == null)
            return new NotFoundResponse(requestUri);
        final File requested = new File(htmlRoot, requestUri.getPath().replaceFirst("(" + rootPath + "/)|(" + rootPath + ")\\b", ""));
        if (!requested.exists())
            return new NotFoundResponse(requestUri);
        final Path requestedPath = requested.toPath();
        final String mimeType = Files.probeContentType(requestedPath);
        if (requested.isDirectory()) {
            return new DirectoryResponse(requested, requestUri);
        }
        if (mimeType == null)
            return new BinaryResponse(Files.readAllBytes(requestedPath), null);
        if (!mimeType.contains("/")) {
            return new BinaryResponse(Files.readAllBytes(requestedPath), mimeType);
        } else {
            final String[] split = mimeType.split("/");
            if (split[0].equals("text") || mimeType.equals("application/javascript") || mimeType.equals("application/response") || mimeType.equals("application/xml")) {
                return new TextResponse(Joiner.on("\n").join(Files.readAllLines(requestedPath)), mimeType);
            } else {
                return new BinaryResponse(Files.readAllBytes(requestedPath), mimeType);
            }
        }
    }

    public void setDefaultPageHandler(final PageHandler defaultPageHandler) {
        this.defaultPageHandler = defaultPageHandler;
    }

    public PageHandler getDefaultPageHandler() {
        return defaultPageHandler;
    }

    public PageHandler getPageHandler(final URI requestUri) {
        for (String path : pageHandlerMap.keySet()) {
            if (requestUri.getPath().matches(path)) {
                return pageHandlerMap.get(path);
            }
        }
        return pageHandlerMap.getOrDefault(requestUri.getPath(), defaultPageHandler);
    }

    public void registerPageHandler(final String path, final PageHandler pageHandler) {
        pageHandlerMap.put(createRegexFromGlob(concatPath(rootPath, path)), pageHandler);
    }

    public void removePageHandler(final String path) {
        pageHandlerMap.remove(createRegexFromGlob(concatPath(rootPath, path)));
    }

    public void protect(final String path, final Authenticator authenticator) {
        contextMap.computeIfAbsent(concatPath(rootPath, path), (l) -> server.createContext(concatPath(rootPath, path), httpHandler)).setAuthenticator(authenticator);
    }

    private String concatPath(final String path, final String path2) {
        if (path.endsWith("/"))
            return path + path2;
        else return path + "/" + path2;
    }

    private String createRegexFromGlob(String glob) {
        StringBuilder out = new StringBuilder("^");
        for (int i = 0; i < glob.length(); ++i) {
            final char c = glob.charAt(i);
            switch (c) {
                case '*':
                    out.append(".*");
                    break;
                case '?':
                    out.append('.');
                    break;
                case '.':
                    out.append("\\.");
                    break;
                case '\\':
                    out.append("\\\\");
                    break;
                default:
                    out.append(c);
            }
        }
        out.append('$');
        return out.toString();
    }

    // <------------------------------------------>

    public static WebApplication quickStart(final InetSocketAddress address, final int backlog, final String rootPath, final File htmlRoot) throws IOException {
        final HttpServer server = HttpServer.create(address, backlog);
        server.setExecutor(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
        server.start();
        return new WebApplication(server, rootPath, htmlRoot);
    }

    public static WebApplication quickStart(final InetSocketAddress address, final int backlog, final String rootPath) throws IOException {
        final HttpServer server = HttpServer.create(address, backlog);
        server.setExecutor(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
        server.start();
        return new WebApplication(server, rootPath);
    }

}
