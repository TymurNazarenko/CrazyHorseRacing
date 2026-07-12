package de.thb.crazyhorseracing.service.object;

import lombok.Getter;
import lombok.Setter;

public class LoginResponse extends Response {
    @Getter
    @Setter
    private String jid;
    public LoginResponse(boolean success, String message, String jid) {
        super(success, message);
        this.jid = jid;
    }

    public LoginResponse(boolean success, String message) {
        this(success, message, null);
    }
}
