package de.exceptionflug.moon.response;

import de.exceptionflug.moon.DomElement;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class TextResponse extends AbstractResponse {

    private String responseText;
    private Map<String, String> replacements = new HashMap<>();

    public TextResponse(final String responseText, final String contentType, final int statusCode) {
        super(responseText.getBytes(StandardCharsets.UTF_8), contentType, statusCode);
        this.responseText = responseText;
        int index = 0;
        while ((index = responseText.indexOf("::%", index+1)) >= 0) {
            final String var = responseText.substring(index, responseText.indexOf("%::", index)+3);
            replacements.put(var, "");
        }
    }

    public TextResponse(final String responseText, final String contentType) {
        super(responseText.getBytes(StandardCharsets.UTF_8), contentType, 200);
        this.responseText = responseText;
        int index = 0;
        while ((index = responseText.indexOf("::%", index+1)) >= 0) {
            final String var = responseText.substring(index, responseText.indexOf("%::", index)+3);
            replacements.put(var, "");
        }
    }

    public Map<String, String> getReplacements() {
        return replacements;
    }

    public void replace(final String key, final String replacement) {
        replacements.put(key, replacement);
    }

    public void replace(final String key, final DomElement element) {
        replacements.put(key, element.toString());
    }

    public String formatResponse() {
        String out = responseText;
        for(final String key : replacements.keySet()) {
            String replacement = replacements.get(key);
            if(replacement == null) {
                replacement = "ERROR";
            }
            out = out.replace(key, replacement);
        }
        return out;
    }

    @Override
    public byte[] getData() {
        return formatResponse().getBytes(StandardCharsets.UTF_8);
    }

    public void setResponseText(final String responseText) {
        this.responseText = responseText;
        setData(responseText.getBytes(StandardCharsets.UTF_8));
    }
}
