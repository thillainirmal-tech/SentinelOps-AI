package dev.thillainirmal.sentinelops.agent;

import dev.thillainirmal.sentinelops.domain.Runbook;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Component
public class RunbookKnowledgeBase {

    private final List<Runbook> runbooks = List.of(
            new Runbook(
                    "http-5xx",
                    "HTTP 5xx and service outage",
                    "503 500 unavailable timeout gateway crash health check pods restart latency",
                    List.of(
                            "Inspect health, error-rate and latency metrics for the affected service.",
                            "Collect recent application logs and correlate the first error with the deployment timeline.",
                            "Check pod readiness, dependency health and resource saturation.",
                            "Restart the deployment only after diagnostics are captured and a human approves the action."
                    )
            ),
            new Runbook(
                    "kafka-lag",
                    "Kafka consumer lag",
                    "kafka lag consumer backlog partition rebalance throughput offset messages",
                    List.of(
                            "Compare producer throughput with consumer processing rate.",
                            "Inspect consumer-group lag per partition and recent rebalance events.",
                            "Check downstream database latency before scaling consumers.",
                            "Scale the consumer deployment only after confirming partition capacity."
                    )
            ),
            new Runbook(
                    "database-saturation",
                    "Database connection saturation",
                    "database postgres mysql connection pool exhausted slow query lock timeout",
                    List.of(
                            "Inspect active connections, pool utilization and acquisition timeouts.",
                            "Identify slow queries and lock contention from database telemetry.",
                            "Reduce nonessential traffic or enable a safe degradation path.",
                            "Change pool limits only with database headroom and human approval."
                    )
            ),
            new Runbook(
                    "payment-failure",
                    "Payment authorization failure",
                    "payment failed fraud authorization decline gateway duplicate charge transaction wallet",
                    List.of(
                            "Check payment-gateway error codes without logging cardholder secrets.",
                            "Compare authorization failures with fraud-rule decisions and dependency health.",
                            "Verify idempotency keys before retrying any transaction.",
                            "Never replay or refund a transaction without explicit operator approval."
                    )
            ),
            new Runbook(
                    "security-event",
                    "Suspected security incident",
                    "security breach unauthorized credential token exfiltration attack suspicious account",
                    List.of(
                            "Preserve relevant audit logs and isolate the affected identity or workload.",
                            "Rotate exposed credentials through the approved secret-management process.",
                            "Review authentication anomalies and access scope.",
                            "Escalate to the security owner before containment actions that affect users."
                    )
            )
    );

    public Runbook retrieve(String query) {
        Map<String, Integer> queryVector = vectorize(query);
        return runbooks.stream()
                .max((left, right) -> Double.compare(
                        cosine(queryVector, vectorize(left.searchableText())),
                        cosine(queryVector, vectorize(right.searchableText()))
                ))
                .orElse(runbooks.get(0));
    }

    double similarity(String left, String right) {
        return cosine(vectorize(left), vectorize(right));
    }

    private Map<String, Integer> vectorize(String value) {
        Map<String, Integer> vector = new HashMap<>();
        for (String token : tokenize(value)) {
            vector.merge(token, 1, Integer::sum);
        }
        return vector;
    }

    private List<String> tokenize(String value) {
        String[] rawTokens = value.toLowerCase(Locale.ROOT).split("[^a-z0-9]+");
        List<String> tokens = new ArrayList<>();
        for (String token : rawTokens) {
            if (token.length() > 2) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    private double cosine(Map<String, Integer> left, Map<String, Integer> right) {
        Set<String> vocabulary = new HashSet<>(left.keySet());
        vocabulary.addAll(right.keySet());
        double dot = 0;
        double leftMagnitude = 0;
        double rightMagnitude = 0;
        for (String term : vocabulary) {
            int leftValue = left.getOrDefault(term, 0);
            int rightValue = right.getOrDefault(term, 0);
            dot += leftValue * rightValue;
            leftMagnitude += leftValue * leftValue;
            rightMagnitude += rightValue * rightValue;
        }
        if (leftMagnitude == 0 || rightMagnitude == 0) {
            return 0;
        }
        return dot / (Math.sqrt(leftMagnitude) * Math.sqrt(rightMagnitude));
    }
}
