# NiFi flow (version-controlled)

`flow.json` is the NiFi canvas — currently `ConsumeAMQP` → `PutFile` — committed
so a fresh checkout gets the whole flow on `docker compose up -d`, with no
clicking and no REST calls.

## How it gets loaded

NiFi keeps its live canvas in `conf/flow.json.gz`, inside the `nifi-conf`
named volume. `docker-compose.yml` overrides the `nifi` container's entrypoint
so that, **only when that file doesn't exist yet** (fresh clone, or after
`docker compose down -v`), it gzips this `flow.json` into place before NiFi
starts. The processors are saved as `RUNNING`, so they start straight away.

An existing volume is never overwritten, so changes made in the UI survive
`docker compose down`/`up` — but they stay local until exported.

## Changing the flow

1. Edit the flow in the NiFi UI (http://localhost:8080/nifi).
2. `./scripts/export-nifi-flow.sh`: copies the live canvas back into this
   `flow.json` (pretty-printed).
3. Review `git diff nifi/flow/flow.json` and commit.

To throw away local changes and go back to the committed flow:
`docker compose down -v && docker compose up -d`.

## Sensitive values

Sensitive processor properties (e.g. `ConsumeAMQP`'s RabbitMQ password) are
stored as `enc{...}`, encrypted with `NIFI_SENSITIVE_PROPS_KEY` from
`docker-compose.yml`. That key is fixed (rather than NiFi's random
per-install default) so this file decrypts on any machine — it's a local-dev
key protecting only local-dev credentials (`guest`/`guest`), not a secret.

NiFi re-encrypts with a fresh random salt every time it saves, so each export
changes the `enc{...}` line even when the password itself hasn't changed.
That's expected and harmless.
