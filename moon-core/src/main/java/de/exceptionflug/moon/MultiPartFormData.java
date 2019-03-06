package de.exceptionflug.moon;


import de.exceptionflug.moon.handler.FormDataHandler;

import java.util.ArrayList;
import java.util.List;

public class MultiPartFormData {

    private final List<FormDataHandler.MultiPart> parts;

    public MultiPartFormData(final List<FormDataHandler.MultiPart> parts) {
        this.parts = parts;
    }

    public FormDataHandler.MultiPart get(final String key) {
        for(final FormDataHandler.MultiPart part : parts) {
            if(part.name.equals(key))
                return part;
        }
        return null;
    }

    public boolean contains(final String key) {
        return get(key) != null;
    }

    public List<FormDataHandler.MultiPart> getParts() {
        return parts;
    }

    public List<FormDataHandler.MultiPart> getMultiple(final String key) {
        final List<FormDataHandler.MultiPart> out = new ArrayList<>();
        for(final FormDataHandler.MultiPart part : parts) {
            if(part.name.equals(key))
                out.add(part);
        }
        return out;
    }
}
