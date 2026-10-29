# SentinelOps AI security model

## Trust boundaries

1. Incident descriptions are untrusted input.
2. Retrieved runbooks are trusted only after repository review.
3. Agent output and proposed commands are untrusted suggestions.
4. Human approval changes proposal state but does not execute infrastructure commands.

## Threats addressed

| Threat | Control |
|---|---|
| Prompt or instruction injection in incident text | Incident text never becomes executable code |
| Hallucinated remediation | Plans are grounded in a small reviewed runbook set |
| Destructive tool invocation | Dangerous actions remain `PROPOSED` until explicit approval |
| Command injection through service names | Commands use fixed placeholders rather than interpolating input |
| Secret disclosure | No credentials are required or stored; guidance warns against sensitive input |
| Silent decision making | Every stage emits evidence in the agent trace |

## Deliberate non-goals

The current release does not execute commands, connect to a cluster or claim autonomous remediation. Those capabilities require an external policy-enforcement layer, scoped identities, immutable audit logs and environment-specific review.
