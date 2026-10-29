package dev.thillainirmal.sentinelops.service;

public class ActionNotFoundException extends RuntimeException {
    public ActionNotFoundException(String actionId) {
        super("Action not found: " + actionId);
    }
}
