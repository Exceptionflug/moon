package de.exceptionflug.moon.response;

public abstract class AbstractResponse {

    private byte[] data;
    private String contentType;
    private int statusCode;
    private boolean cancelled;

    public AbstractResponse(final byte[] rawData, final String contentType, final int statusCode) {
        this.data = rawData;
        this.contentType = contentType;
        this.statusCode = statusCode;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(final byte[] data) {
        this.data = data;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(final int statusCode) {
        this.statusCode = statusCode;
    }

    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isCancelled() {
        return cancelled;
    }
}
