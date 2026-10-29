# Security policy

## Reporting

Please do not open a public issue for a vulnerability. Use GitHub's private vulnerability reporting feature when available, or contact the maintainer through the email listed on the GitHub profile.

Include the affected version, reproduction steps, impact and any suggested mitigation. You should receive an acknowledgement within five business days.

## Scope

SentinelOps never executes proposed shell commands. If you extend it with tool execution, keep credentials outside model context, use allow-listed read-only tools by default and require signed human approval for state changes.
