package de.thb.crazyhorseracing.service.response;

import lombok.Getter;
import lombok.Setter;

public class LoginResponse extends ActionResponse {
    @Getter
    @Setter
    private String AuthCookie;
    public LoginResponse(boolean success, String message, String AuthCookie) {
        super(success, message);
        this.AuthCookie = AuthCookie;
    }

    public LoginResponse(boolean success, String message) {
        this(success, message, null);
    }
}
