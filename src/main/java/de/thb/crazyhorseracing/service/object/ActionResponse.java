package de.thb.crazyhorseracing.service.object;

public class ActionResponse {
    public final boolean success;
    public final String message;
    public ActionResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
