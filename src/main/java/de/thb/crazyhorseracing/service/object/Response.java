package de.thb.crazyhorseracing.service.object;

public class Response {
    public final boolean success;
    public final String message;
    public Response(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
