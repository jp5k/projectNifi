# Sample data

Fictional stocks-and-shares data for exercising the project's messaging/dataflow
pieces. No real companies, symbols, or market data — purely for learning.

- **`stocks.json`** — reference/master data (10 fictional companies), each with a
  `sector` and a `classification` label (`PUBLIC` / `INTERNAL` / `RESTRICTED`).
  Intended to seed `stock-service`.
- **`price-updates.json`** — a short simulated tick stream (6 updates each for 5
  of the stocks above), each carrying the same `classification` as its stock and
  a `routingKey` (`stock.price.<classification>`) matching the topic-exchange
  routing scheme described in `docs/plan.md`. Intended to be published onto
  RabbitMQ to exercise the NiFi flow and `notification-service`.
- **`price-update.xml`** — one price update (NVTD) in XML form, matching the
  shape NiFi's `ConvertRecord` (JSON reader → XML writer) would produce from a
  message on the queue. Input to the XSLT stylesheet at `nifi/xslt/price-report.xsl`.
- **`price-report-example.xml`** — the verified output of running that stylesheet
  against `price-update.xml` (via the JDK's built-in XSLT processor) — use it to
  confirm NiFi's `TransformXml` step produces the same result.

See `docs/plan.md` for how these get used (Foundations seeding, Messaging &
Dataflow, and Data Classification & Access Control milestones).
