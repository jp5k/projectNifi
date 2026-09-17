package com.jp5k.projectnifi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Incoming shape for {@code POST /stocks/{symbol}/price-updates} — a single
 * simulated tick to publish onto RabbitMQ for the stock at the path's
 * {@code symbol}.
 *
 * <p>{@code symbol} itself isn't a field here; it comes from the URL path,
 * the same way {@link StockRequest} handles it for {@code PUT
 * /stocks/{symbol}} — kept out of the body so there's only one place a client
 * could get it wrong. {@code timestamp} is client-supplied rather than
 * server-generated, since replaying a fixed historical stream (see
 * {@code sample-data/price-updates.json}) needs each tick to carry its
 * original time.
 *
 * @param timestamp when the tick occurred
 * @param price     the tick's price; must be present and strictly greater
 *                  than zero
 * @param volume    shares traded at this tick; must be present and strictly
 *                  greater than zero
 */
public record StockPriceUpdateRequest(
        @NotNull Instant timestamp,
        @NotNull @Positive BigDecimal price,
        @NotNull @Positive Integer volume) {
}
