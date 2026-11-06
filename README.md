# incidentai-checkout-api

**Synthetic demo service** used by the IncidentAI Slack incident-response demo.
Nothing here runs in production.

- `v2.13.4`: last good release
- `v2.14.0`: "perf" change that shrinks the production DB connection pool (50 → 5) and moves the
  payment retry *inside* the open DB transaction, which exhausts the pool under load (the incident)

Rollbacks in this repo are opened and merged automatically from Slack (`#inc-live-demo`).
