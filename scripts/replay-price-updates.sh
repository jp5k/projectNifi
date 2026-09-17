#!/usr/bin/env bash
# Replays sample-data/price-updates.json through stock-service's
# POST /stocks/{symbol}/price-updates endpoint, to generate a stream of
# StockPriceUpdate events on RabbitMQ for manual verification (see
# docs/plan.md's Messaging & Dataflow milestone). Requires stock-service to
# be running (default http://localhost:8081) and its seeded stocks to already
# exist (StockDataSeeder does this on startup) — an update for an unknown
# symbol is rejected with 404.
#
# Usage: ./scripts/replay-price-updates.sh [base-url]

set -euo pipefail

BASE_URL="${1:-http://localhost:8081}"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PRICE_UPDATES_FILE="$SCRIPT_DIR/../sample-data/price-updates.json"

jq -c '.[]' "$PRICE_UPDATES_FILE" | while read -r update; do
    symbol=$(jq -r '.symbol' <<<"$update")
    body=$(jq -c '{timestamp, price, volume}' <<<"$update")

    status=$(curl -s -o /dev/null -w '%{http_code}' \
        -X POST "$BASE_URL/stocks/$symbol/price-updates" \
        -H 'Content-Type: application/json' \
        -d "$body")

    echo "$symbol -> $status: $body"
done
