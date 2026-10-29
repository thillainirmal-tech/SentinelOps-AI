# Contributing to SentinelOps AI

Thanks for helping make incident response safer and easier to understand.

## Good first contributions

- Add a runbook with clear signals and evidence-first steps.
- Add an incident fixture that improves triage or retrieval testing.
- Improve dashboard accessibility or responsive behavior.
- Add a safe, read-only observability integration.
- Clarify documentation with a reproducible example.

## Workflow

1. Open or claim an issue so work is not duplicated.
2. Fork the repository and create a focused branch.
3. Keep state-changing commands behind an explicit approval gate.
4. Add or update tests.
5. Run `mvn verify` before opening a pull request.

## Design rules

- Models and heuristics advise; they do not own production credentials.
- Retrieval must expose its evidence.
- Dangerous actions default to blocked.
- A failed AI integration must not prevent operators from seeing the incident.
- Never include real credentials, customer data or proprietary runbooks in examples.

By participating, you agree to follow the [Code of Conduct](CODE_OF_CONDUCT.md).
