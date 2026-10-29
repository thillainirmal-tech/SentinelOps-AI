package dev.thillainirmal.sentinelops.agent;

import dev.thillainirmal.sentinelops.domain.IncidentRequest;
import dev.thillainirmal.sentinelops.domain.Severity;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TriageAgent {

    private static final Map<String, Integer> SIGNALS = new LinkedHashMap<>();

    static {
        SIGNALS.put("data loss", 6);
        SIGNALS.put("security breach", 6);
        SIGNALS.put("unauthorized", 5);
        SIGNALS.put("all users", 5);
        SIGNALS.put("payment failed", 5);
        SIGNALS.put("outage", 5);
        SIGNALS.put("unavailable", 4);
        SIGNALS.put("503", 4);
        SIGNALS.put("500", 3);
        SIGNALS.put("timeout", 3);
        SIGNALS.put("kafka lag", 3);
        SIGNALS.put("latency", 2);
        SIGNALS.put("error", 1);
        SIGNALS.put("warning", 1);
    }

    public TriageResult analyze(IncidentRequest request) {
        String text = (request.title() + " " + request.description()).toLowerCase(Locale.ROOT);
        Map<String, Integer> matches = SIGNALS.entrySet().stream()
                .filter(entry -> text.contains(entry.getKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (left, right) -> left,
                        LinkedHashMap::new
                ));

        int score = matches.values().stream().mapToInt(Integer::intValue).sum();
        Severity severity = switch (score) {
            case 0, 1 -> Severity.LOW;
            case 2, 3 -> Severity.MEDIUM;
            case 4, 5, 6 -> Severity.HIGH;
            default -> Severity.CRITICAL;
        };
        double confidence = Math.min(0.98, 0.56 + (matches.size() * 0.08) + (score * 0.025));
        String evidence = matches.isEmpty()
                ? "No high-risk signal matched; assigned a conservative low severity."
                : "Matched signals: " + String.join(", ", matches.keySet());

        return new TriageResult(severity, Math.round(confidence * 100.0) / 100.0, evidence);
    }
}
