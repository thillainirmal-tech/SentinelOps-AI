package dev.thillainirmal.sentinelops.domain;

public record AgentStep(
        AgentRole agent,
        String summary,
        String evidence
) {
}
