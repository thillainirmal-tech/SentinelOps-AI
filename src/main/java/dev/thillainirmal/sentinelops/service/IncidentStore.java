package dev.thillainirmal.sentinelops.service;

import dev.thillainirmal.sentinelops.domain.ActionProposal;
import dev.thillainirmal.sentinelops.domain.IncidentAnalysis;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class IncidentStore {

    private final Map<String, IncidentAnalysis> analyses = new ConcurrentHashMap<>();

    public IncidentAnalysis save(IncidentAnalysis analysis) {
        analyses.put(analysis.incidentId(), analysis);
        return analysis;
    }

    public IncidentAnalysis find(String incidentId) {
        IncidentAnalysis analysis = analyses.get(incidentId);
        if (analysis == null) {
            throw new IncidentNotFoundException(incidentId);
        }
        return analysis;
    }

    public IncidentAnalysis approve(String incidentId, String actionId) {
        return analyses.compute(incidentId, (id, analysis) -> {
            if (analysis == null) {
                throw new IncidentNotFoundException(incidentId);
            }
            List<ActionProposal> updated = analysis.actions().stream()
                    .map(action -> action.id().equals(actionId) ? action.approve() : action)
                    .toList();
            boolean found = analysis.actions().stream().anyMatch(action -> action.id().equals(actionId));
            if (!found) {
                throw new ActionNotFoundException(actionId);
            }
            return analysis.withActions(updated);
        });
    }
}
