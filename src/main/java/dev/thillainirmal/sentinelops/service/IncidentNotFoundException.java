package dev.thillainirmal.sentinelops.service;

public class IncidentNotFoundException extends RuntimeException {
    public IncidentNotFoundException(String incidentId) {
        super("Incident not found: " + incidentId);
    }
}
