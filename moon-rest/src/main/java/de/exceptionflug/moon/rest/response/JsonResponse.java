package de.exceptionflug.moon.rest.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import de.exceptionflug.moon.response.AbstractResponse;

import java.nio.charset.StandardCharsets;

public class JsonResponse<T> extends AbstractResponse {

    private T body;

    public JsonResponse(final T body) {
        super(null, "application/json", 200);
        this.body = body;
    }

    public JsonResponse(final T body, final int statusCode) {
        super(null, "application/json", statusCode);
        this.body = body;
    }

    public T getBody() {
        return body;
    }

    public void serialize(final ObjectMapper mapper) {
        if(body == null) {
            setData(JsonNodeFactory.instance.objectNode().toString().getBytes(StandardCharsets.UTF_8));
            return;
        }
        setData(mapper.valueToTree(body).toString().getBytes(StandardCharsets.UTF_8));
    }

}
