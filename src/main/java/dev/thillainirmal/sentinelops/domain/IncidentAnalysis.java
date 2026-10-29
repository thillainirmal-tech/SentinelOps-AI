package dev.thillainirmal.sentinelops.domain;

import java.time.Instant;
import java.util.List;

public record IncidentAnalysis(
        String incidentId,
        String title,
        String service,
        Severity severity,
        double confidence,
        String summary,
        String matchedRunbook,
        List<AgentStep> agentSteps,
        List<ActionProposal> actions,
        Instant createdAt
) {
    public IncidentAnalysis withActions(List<ActionProposal> updatedActions) {
        return new IncidentAnalysis(
                incidentId,
                title,
                service,
                severity,
                confidence,
                summary,
                matchedRunbook,
                agentSteps,
                List.copyOf(updatedActions),
                createdAt
        );
    }
}
