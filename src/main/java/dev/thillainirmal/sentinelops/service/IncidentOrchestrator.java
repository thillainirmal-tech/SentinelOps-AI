package dev.thillainirmal.sentinelops.service;

import dev.thillainirmal.sentinelops.agent.RunbookKnowledgeBase;
import dev.thillainirmal.sentinelops.agent.SafetyAgent;
import dev.thillainirmal.sentinelops.agent.TriageAgent;
import dev.thillainirmal.sentinelops.agent.TriageResult;
import dev.thillainirmal.sentinelops.domain.AgentRole;
import dev.thillainirmal.sentinelops.domain.AgentStep;
import dev.thillainirmal.sentinelops.domain.IncidentAnalysis;
import dev.thillainirmal.sentinelops.domain.IncidentRequest;
import dev.thillainirmal.sentinelops.domain.Runbook;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class IncidentOrchestrator {

    private final TriageAgent triageAgent;
    private final RunbookKnowledgeBase knowledgeBase;
    private final SafetyAgent safetyAgent;
    private final IncidentStore store;
    private final Counter analyzedCounter;

    public IncidentOrchestrator(
            TriageAgent triageAgent,
            RunbookKnowledgeBase knowledgeBase,
            SafetyAgent safetyAgent,
            IncidentStore store,
            MeterRegistry meterRegistry
    ) {
        this.triageAgent = triageAgent;
        this.knowledgeBase = knowledgeBase;
        this.safetyAgent = safetyAgent;
        this.store = store;
        this.analyzedCounter = meterRegistry.counter("sentinelops.incidents.analyzed");
    }

    public IncidentAnalysis analyze(IncidentRequest request) {
        TriageResult triage = triageAgent.analyze(request);
        Runbook runbook = knowledgeBase.retrieve(request.title() + " " + request.description());
        String service = request.normalizedService();
        var actions = safetyAgent.review(runbook, service);

        List<AgentStep> steps = List.of(
                new AgentStep(AgentRole.TRIAGE,
                        "Assigned " + triage.severity() + " severity with "
                                + Math.round(triage.confidence() * 100) + "% confidence.",
                        triage.evidence()),
                new AgentStep(AgentRole.RUNBOOK_RETRIEVAL,
                        "Selected the most relevant operational runbook.",
                        runbook.title() + " — " + runbook.steps().get(0)),
                new AgentStep(AgentRole.SAFETY_REVIEW,
                        "Separated read-only diagnostics from state-changing actions.",
                        "Every dangerous proposal remains blocked until a human approves it."),
                new AgentStep(AgentRole.RESOLUTION_PLANNING,
                        "Produced an evidence-first response plan.",
                        "The plan contains " + actions.size() + " actions and preserves an approval trail.")
        );

        String summary = "%s incident affecting %s. Start with evidence collection, validate the matched %s runbook, and require approval before production changes."
                .formatted(triage.severity(), service, runbook.title());

        IncidentAnalysis analysis = new IncidentAnalysis(
                UUID.randomUUID().toString(),
                request.title().trim(),
                service,
                triage.severity(),
                triage.confidence(),
                summary,
                runbook.title(),
                steps,
                actions,
                Instant.now()
        );
        analyzedCounter.increment();
        return store.save(analysis);
    }

    public IncidentAnalysis approve(String incidentId, String actionId) {
        return store.approve(incidentId, actionId);
    }

    public IncidentAnalysis find(String incidentId) {
        return store.find(incidentId);
    }
}
