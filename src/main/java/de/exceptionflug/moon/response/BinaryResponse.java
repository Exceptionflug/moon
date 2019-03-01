package de.exceptionflug.moon.response;

public class BinaryResponse extends AbstractResponse {

    public BinaryResponse(final byte[] rawData, final String contentType) {
        super(rawData, contentType, 200);
    }

}
