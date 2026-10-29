package dev.thillainirmal.sentinelops.agent;

import dev.thillainirmal.sentinelops.domain.IncidentRequest;
import dev.thillainirmal.sentinelops.domain.Severity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TriageAgentTest {

    private final TriageAgent agent = new TriageAgent();

    @Test
    void identifiesCriticalCustomerImpact() {
        TriageResult result = agent.analyze(new IncidentRequest(
                "Payment outage",
                "All users receive 503 and payment failed messages because the gateway is unavailable.",
                "checkout"
        ));

        assertThat(result.severity()).isEqualTo(Severity.CRITICAL);
        assertThat(result.confidence()).isGreaterThanOrEqualTo(0.8);
        assertThat(result.evidence()).contains("all users", "503", "payment failed");
    }

    @Test
    void keepsWeakSignalsConservative() {
        TriageResult result = agent.analyze(new IncidentRequest(
                "Cache observation",
                "A warning appeared once and users are not affected.",
                "catalog"
        ));

        assertThat(result.severity()).isEqualTo(Severity.LOW);
    }
}
