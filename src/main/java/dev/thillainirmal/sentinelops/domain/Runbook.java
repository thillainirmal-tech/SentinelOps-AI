package dev.thillainirmal.sentinelops.domain;

import java.util.List;

public record Runbook(
        String id,
        String title,
        String signals,
        List<String> steps
) {
    public String searchableText() {
        return title + " " + signals + " " + String.join(" ", steps);
    }
}
