package dev.thillainirmal.sentinelops.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record IncidentRequest(
        @NotBlank @Size(max = 120) String title,
        @NotBlank @Size(max = 4000) String description,
        @Size(max = 80) String service
) {
    public String normalizedService() {
        return service == null || service.isBlank() ? "unknown-service" : service.trim();
    }
}
