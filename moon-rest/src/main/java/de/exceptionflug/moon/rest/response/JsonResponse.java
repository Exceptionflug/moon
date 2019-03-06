package de.exceptionflug.moon.rest.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.exceptionflug.moon.response.AbstractResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JsonResponse<T> extends AbstractResponse {

    private T body;

    public JsonResponse(final T body) {
        super(null, "application/response", 200);
        this.body = body;
    }

    public JsonResponse(final T body, final int statusCode) {
        super(null, "application/response", statusCode);
        this.body = body;
    }

    public T getBody() {
        return body;
    }

    public void serialize(final ObjectMapper mapper) throws JsonProcessingException {
        final ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.set("type", mapper.valueToTree(body.getClass().getName()));
        node.set("value", mapper.valueToTree(body));
        setData(node.toString().getBytes(StandardCharsets.UTF_8));
    }

    public void deserialize(final ObjectMapper mapper) throws IOException, ClassNotFoundException {
        final ObjectNode node = (ObjectNode) mapper.readTree(new String(getData(), StandardCharsets.UTF_8));
        body = (T) mapper.convertValue(node.get("value"), Class.forName(node.get("type").textValue()));
    }

}
