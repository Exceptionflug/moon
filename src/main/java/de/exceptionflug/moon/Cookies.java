package de.exceptionflug.moon;

import com.sun.net.httpserver.Headers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cookies {

    private final Map<String, Cookie> cookies = new HashMap<>();

    public Cookies(final List<String> cookies) {
        if(cookies == null)
            return;
        for(final String entry : cookies) {
            final String[] rawCookieParams = entry.split(";");
            for (final String rawCookieParam : rawCookieParams) {
                final String[] rawCookieParamNameAndValue = rawCookieParam.trim().split("=");
                if(rawCookieParamNameAndValue.length < 2)
                    continue;
                final String cookieName = rawCookieParamNameAndValue[0].trim();
                final String cookieValue = rawCookieParamNameAndValue[1].trim();
                final Cookie paramCookie = new Cookie(cookieName, cookieValue);
                this.cookies.put(cookieName, paramCookie);
            }
        }
    }

    public Cookie get(final String key) {
        return cookies.get(key);
    }

    public Cookie getOrSetDefault(final String key, final Cookie defaultValue) {
        return cookies.computeIfAbsent(key, s -> defaultValue);
    }

    public Cookie getOrSetDefault(final String key, final String defaultValue) {
        return cookies.computeIfAbsent(key, s -> new Cookie(key, defaultValue));
    }

    public void remove(final String key) {
        cookies.remove(key);
    }

    public void set(final Cookie value) {
        cookies.put(value.getId(), value);
    }

    public void set(final String key, final String value) {
        cookies.put(key, new Cookie(key, value));
    }

    public boolean isSet(final String key) {
        return cookies.containsKey(key);
    }

    public List<String> exportList() {
        final List<String> out = new ArrayList<>();
        for(final Cookie cookie : cookies.values()) {
            out.add(cookie.toString());
        }
        return out;
    }

    public void setCookies(final Headers headers) {
        for(final Cookie cookie : cookies.values()) {
            headers.add("Set-Cookie", cookie.toString());
        }
    }

    public int size() {
        return cookies.size();
    }

    public static class Cookie {

        private final String id, value;
        private int maxAge = -1;
        private String expires, path, domain;
        private boolean secure;

        public Cookie(final String id, final String value) {
            this.id = id;
            this.value = value;
        }

        public String getId() {
            return id;
        }

        public String getValue() {
            return value;
        }

        public int getMaxAge() {
            return maxAge;
        }

        public Cookie setMaxAge(final int maxAge) {
            this.maxAge = maxAge;
            return this;
        }

        public String getExpires() {
            return expires;
        }

        public Cookie setExpires(final String expires) {
            this.expires = expires;
            return this;
        }

        public String getPath() {
            return path;
        }

        public Cookie setPath(String path) {
            this.path = path;
            return this;
        }

        public boolean isSecure() {
            return secure;
        }

        public Cookie setSecure(boolean secure) {
            this.secure = secure;
            return this;
        }

        public String getDomain() {
            return domain;
        }

        public Cookie setDomain(String domain) {
            this.domain = domain;
            return this;
        }

        @Override
        public String toString() {
            final StringBuilder out = new StringBuilder(id).append("=").append(value);
            if(maxAge != -1) {
                out.append("; Max-Age=").append(maxAge);
            }
            if(expires != null) {
                out.append("; Expires=").append(expires);
            }
            if(path != null) {
                out.append("; Path=").append(path);
            }
            if(secure) {
                out.append("; secure");
            }
            if(domain != null) {
                out.append("; domain=").append(domain);
            }
            return out.toString();
        }
    }

}
