# Evaluation guide

SentinelOps begins with deterministic behavior so safety and retrieval can be evaluated without model variance.

## Core checks

- Critical customer-wide outages should be classified `CRITICAL`.
- Weak observations without impact should remain `LOW` or `MEDIUM`.
- Kafka symptoms should retrieve the Kafka consumer-lag runbook.
- Payment authorization symptoms should retrieve the payment-failure runbook.
- State-changing proposals should never begin as `APPROVED`.
- Approval should update only the requested action.

## Extending evaluation for an LLM provider

When a model adapter is added, preserve these deterministic tests and add a fixed incident dataset. Track severity agreement, retrieval precision, unsupported claims, unsafe-action rate, latency and token cost. A model upgrade should be blocked if it increases unsafe-action rate even when prose quality improves.
