#!/usr/bin/env bash
# Copies the running NiFi container's live canvas (conf/flow.json.gz) back
# into the repo as nifi/flow/flow.json — the file docker-compose.yml seeds a
# fresh NiFi from. Run it after changing the flow in the NiFi UI, then review
# and commit the diff. Pretty-printed with jq so diffs stay readable. Requires
# the nifi container to be running (docker compose up -d); see
# nifi/flow/README.md.
#
# Usage: ./scripts/export-nifi-flow.sh

set -euo pipefail

CONTAINER="projectnifi-nifi"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
FLOW_FILE="$SCRIPT_DIR/../nifi/flow/flow.json"

# Write to a temp file first so a failed export never truncates the
# committed flow.
tmp=$(mktemp)
trap 'rm -f "$tmp"' EXIT

docker exec "$CONTAINER" cat /opt/nifi/nifi-current/conf/flow.json.gz \
    | gunzip | jq . >"$tmp"
mv "$tmp" "$FLOW_FILE"

echo "Exported $CONTAINER's flow to nifi/flow/flow.json"
