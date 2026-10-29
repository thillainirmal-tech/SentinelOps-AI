package dev.thillainirmal.sentinelops.agent;

import dev.thillainirmal.sentinelops.domain.ActionProposal;
import dev.thillainirmal.sentinelops.domain.ActionRisk;
import dev.thillainirmal.sentinelops.domain.ActionStatus;
import dev.thillainirmal.sentinelops.domain.Runbook;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Component
public class SafetyAgent {

    public List<ActionProposal> review(Runbook runbook, String service) {
        List<ActionProposal> proposals = new ArrayList<>();
        proposals.add(proposal(
                "Collect diagnostics",
                "kubectl logs deployment/<service> --since=15m",
                ActionRisk.SAFE,
                "Read-only evidence collection should happen before remediation."
        ));
        proposals.add(proposal(
                "Inspect service metrics",
                "open dashboard: service=<service>, window=30m",
                ActionRisk.SAFE,
                "Metrics validate impact and reveal whether the incident is still active."
        ));

        String text = runbook.searchableText().toLowerCase(Locale.ROOT);
        if (text.contains("restart")) {
            proposals.add(proposal(
                    "Restart deployment",
                    "kubectl rollout restart deployment/<service>",
                    ActionRisk.DANGEROUS,
                    "A restart changes production state and therefore requires human approval."
            ));
        } else if (text.contains("scale")) {
            proposals.add(proposal(
                    "Scale consumer deployment",
                    "kubectl scale deployment/<service> --replicas=<approved-count>",
                    ActionRisk.DANGEROUS,
                    "Scaling can amplify downstream load and requires capacity review."
            ));
        } else {
            proposals.add(proposal(
                    "Escalate to service owner",
                    "notify owner for <service>",
                    ActionRisk.REVIEW,
                    "The runbook requires a domain owner before state-changing remediation."
            ));
        }
        return List.copyOf(proposals);
    }

    private ActionProposal proposal(String title, String command, ActionRisk risk, String rationale) {
        return new ActionProposal(
                UUID.randomUUID().toString().substring(0, 8),
                title,
                command,
                risk,
                risk == ActionRisk.SAFE ? ActionStatus.APPROVED : ActionStatus.PROPOSED,
                rationale
        );
    }
}
