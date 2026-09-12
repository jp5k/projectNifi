# projectNifi

Java Spring Boot project. See [`docs/plan.md`](docs/plan.md) for the roadmap
and architecture, and [`docs/how-it-works.md`](docs/how-it-works.md) for a
plain-English walkthrough.

## Stack

- Java 21
- Spring Boot 3.5.0
- Maven (multi-module: root `pom.xml` is a `packaging=pom` parent aggregator)
- Spring Web, Spring Data JPA, Bean Validation, H2 (in-memory database)

## Prerequisites

- JDK 21 (`JAVA_HOME` should point to a JDK 21 install, e.g. `/usr/lib/jvm/java-1.21.0-openjdk-amd64`)

## Running

Run a single module (from the repo root):

```bash
./mvnw -pl stock-service spring-boot:run
```

`stock-service` listens on **:8081** (`http://localhost:8081/stocks`, H2
console at `/h2-console`). As more modules land (`sector-service` :8082,
`notification-service` :8083) run each the same way, module name substituted.

## Testing

Runs every module in the reactor build, including the JaCoCo coverage check
bound to `verify`:

```bash
./mvnw verify
```

Dependency vulnerability scanning (OWASP Dependency-Check) is opt-in, not part
of `verify` — see the `security` profile comment in the root `pom.xml`:

```bash
./mvnw verify -Psecurity
```

## Features

_(documented here as they're added)_
