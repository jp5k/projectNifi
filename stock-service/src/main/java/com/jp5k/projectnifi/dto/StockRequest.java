package com.jp5k.projectnifi.dto;

import java.math.BigDecimal;

/**
 * Incoming shape for creating or replacing a {@code Stock} over the REST API.
 *
 * <p>Kept separate from the {@code Stock} JPA entity on purpose: the request
 * body is a <em>contract with API clients</em>, whereas the entity is a
 * persistence concern. Binding requests straight onto the entity would mean
 * every column rename, added audit field, or relationship change risked
 * silently altering the public API (and vice versa), and it would let a client
 * set fields it has no business setting. A dedicated record makes the accepted
 * fields explicit and reviewable in one place.
 *
 * <p>A record (rather than a mutable class) because a request body is read
 * once, immediately mapped to an entity, and then discarded — it never needs
 * to change after binding.
 *
 * <p>No validation annotations yet — {@code spring-boot-starter-validation}
 * and {@code @Valid} arrive in the next roadmap item ("Input validation").
 * Until then a malformed body still surfaces as Spring's default error.
 *
 * @param symbol    ticker symbol, e.g. {@code "NVTD"}. Used as the identifier
 *                  on create ({@code POST /stocks}); ignored on replace
 *                  ({@code PUT /stocks/{symbol}}), where the path segment is
 *                  authoritative so a stock can never be renamed out from
 *                  under its URL.
 * @param name      full company name, e.g. {@code "NovaTech Dynamics"}
 * @param sector    sector name, e.g. {@code "Technology"}
 * @param basePrice reference price the simulated ticks are generated around
 */
public record StockRequest(String symbol, String name, String sector, BigDecimal basePrice) {
}
