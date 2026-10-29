package dev.thillainirmal.sentinelops.domain;

public record ActionProposal(
        String id,
        String title,
        String command,
        ActionRisk risk,
        ActionStatus status,
        String rationale
) {
    public ActionProposal approve() {
        return new ActionProposal(id, title, command, risk, ActionStatus.APPROVED, rationale);
    }
}
