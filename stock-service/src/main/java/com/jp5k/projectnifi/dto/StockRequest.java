package com.jp5k.projectnifi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
 * <p>The constraints below are Jakarta Bean Validation annotations; they only
 * take effect where the controller marks the parameter {@code @Valid}. A
 * violation currently surfaces as Spring's default {@code 400 Bad Request} —
 * turning that into a curated error body is the next roadmap item
 * ("Centralized exception handling"). Constraints are kept deliberately light
 * for this fictional-data project: presence checks, plus a sign check on the
 * price (a stock priced at zero or below is nonsensical).
 *
 * @param symbol    ticker symbol, e.g. {@code "NVTD"}. Must be non-blank on
 *                  create ({@code POST /stocks}), where it becomes the
 *                  identifier. On replace ({@code PUT /stocks/{symbol}}) the
 *                  path segment is authoritative and the body value is ignored
 *                  by the mapper — but it is still validated here, so a
 *                  {@code PUT} body must carry a non-blank {@code symbol} too
 *                  (send the same symbol as the path).
 * @param name      full company name, e.g. {@code "NovaTech Dynamics"}
 * @param sector    sector name, e.g. {@code "Technology"}
 * @param basePrice reference price the simulated ticks are generated around;
 *                  must be present and strictly greater than zero
 */
public record StockRequest(
        @NotBlank String symbol,
        @NotBlank String name,
        @NotBlank String sector,
        @NotNull @Positive BigDecimal basePrice) {
}
