# How projectNifi Works (Plain English)

A companion to [`plan.md`](plan.md) — that doc tracks *what's being built and in
what order*; this one explains *what the finished thing actually does*, for
anyone (including future-you) who needs a refresher without wading through
architecture diagrams.

## The one-line version

It's a pretend stock market feed. Fake companies' share prices tick up and
down, and the system's job is to **move each price update from where it's
created to the people/systems allowed to see it** — while along the way,
transforming it and showing you every step of that journey happening.

Nothing here is real money, real companies, or real trading. It's a safe
sandbox for learning how modern backend systems talk to each other.

## The cast of characters

| Component | Think of it as... | Its job |
|---|---|---|
| **stock-service** | The front desk | Keeps the master list of fake companies and their share prices. This is where a price update is born. |
| **sector-service** | The reference librarian | Keeps a list of valid industries (Technology, Energy, Healthcare...). When stock-service adds a company, it asks this service "is that a real industry?" before accepting it. |
| **RabbitMQ** | The post office | Doesn't know or care what's *in* a price update — it just reliably delivers it to whoever has signed up to receive that type of mail. |
| **NiFi** | The processing plant | Takes each price update off the conveyor belt, reshapes it into a tidier "report" format, and routes it down the correct chute depending on its label. You watch this happening live on a visual canvas. |
| **notification-service** | The watcher | Sits and listens for price updates flying past, and keeps a log of what it's seen — like a security guard's notebook. |

## The journey of a single price update

Imagine a fake company, **NovaTech Dynamics (NVTD)**, and its share price just
moved to £142.50.

1. **It's created.** Someone (you, via a web request) tells `stock-service`:
   "NovaTech's new price is £142.50."
2. **A quick background check.** Before accepting a *new* company into the
   system, `stock-service` double-checks with `sector-service`: "Is
   'Technology' a real sector I know about?" — a quick yes/no conversation
   between the two services, done instantly, while you wait for your answer.
3. **It's dropped in the post.** `stock-service` hands the price update to
   RabbitMQ, with a label stuck on it saying how sensitive it is — `PUBLIC`,
   `INTERNAL`, or `RESTRICTED` (more on this below).
4. **The post office sorts it.** RabbitMQ looks at the label and only
   delivers the update to mailboxes that are allowed to receive that label.
   Nobody without permission ever sees a `RESTRICTED` update.
5. **Two things happen at once, independently:**
   - **NiFi** picks it up, checks the label matches what it's allowed to
     process, converts it into a neater "report" format, and shows the whole
     journey visually on its canvas — like watching a parcel move along a
     conveyor belt with cameras at every station.
   - **notification-service** separately picks up its own copy (if it's
     allowed to) and just quietly logs it — "NVTD updated to £142.50 at
     09:30."
6. **Nothing gets lost, nothing gets seen by the wrong eyes.** Each piece only
   does its one job, and only sees what it's permitted to see.

## Why the "labels" exist

Not every price update should be visible to everyone — that's the whole point
of the classification labels:

- **PUBLIC** — anyone can see it, like a headline on a news ticker.
- **INTERNAL** — for people with a bit more clearance, like a company memo.
- **RESTRICTED** — sensitive, only for those who specifically need it.

This is enforced in **two places**, on purpose, to demonstrate two different
techniques:
- **At the front desk** (`stock-service`) — it checks who's asking and only
  shows them what their clearance allows.
- **At the post office** (RabbitMQ) — mailboxes are only allowed to subscribe
  to the labels they're cleared for, so a `RESTRICTED` update *physically
  never arrives* at an unauthorized mailbox, regardless of what the app tries
  to do.

## The two viewing windows

You get to watch two parts of this happen live in a browser, with no coding
required to look:
- **RabbitMQ's dashboard** — see the post office sorting room: what's queued,
  what's been delivered.
- **NiFi's canvas** — watch each price update visually flow through the
  processing plant step by step.

## What's fictional

Every company, symbol, and price in this project is made up (`NVTD`, `BLHE`,
`SLBT`, etc.) — there's no connection to real markets or real financial data.
It exists purely to give the pipeline something realistic-feeling to move
around while you learn.
