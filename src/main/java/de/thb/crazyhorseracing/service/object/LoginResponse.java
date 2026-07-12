package de.thb.crazyhorseracing.service.object;

public record LoginResponse(boolean success, String jid, String error, String successText) {}
