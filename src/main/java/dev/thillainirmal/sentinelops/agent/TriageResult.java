package dev.thillainirmal.sentinelops.agent;

import dev.thillainirmal.sentinelops.domain.Severity;

public record TriageResult(Severity severity, double confidence, String evidence) {
}
