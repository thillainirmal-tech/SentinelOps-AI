package dev.thillainirmal.sentinelops.web;

import dev.thillainirmal.sentinelops.domain.IncidentAnalysis;
import dev.thillainirmal.sentinelops.domain.IncidentRequest;
import dev.thillainirmal.sentinelops.service.IncidentOrchestrator;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentOrchestrator orchestrator;

    public IncidentController(IncidentOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping("/analyze")
    @ResponseStatus(HttpStatus.CREATED)
    public IncidentAnalysis analyze(@Valid @RequestBody IncidentRequest request) {
        return orchestrator.analyze(request);
    }

    @GetMapping("/{incidentId}")
    public IncidentAnalysis find(@PathVariable String incidentId) {
        return orchestrator.find(incidentId);
    }

    @PostMapping("/{incidentId}/actions/{actionId}/approve")
    public IncidentAnalysis approve(
            @PathVariable String incidentId,
            @PathVariable String actionId
    ) {
        return orchestrator.approve(incidentId, actionId);
    }

    @PostMapping("/demo")
    @ResponseStatus(HttpStatus.CREATED)
    public IncidentAnalysis demo() {
        return orchestrator.analyze(new IncidentRequest(
                "Checkout API outage",
                "All users receive HTTP 503 during payment authorization. Latency increased after the latest deployment and the payment gateway is timing out.",
                "checkout-service"
        ));
    }
}
