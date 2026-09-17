package com.jp5k.projectnifi.dto;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * A single simulated price tick, published onto RabbitMQ (see
 * {@code config/RabbitConfig} for the exchange/queue it's sent to) rather
 * than persisted — {@code Stock#basePrice} is a fixed reference price, not
 * something a tick updates.
 *
 * <p>Serialized straight to JSON on the wire by the {@code Jackson2JsonMessageConverter}
 * configured in {@code RabbitConfig}, so this record's field names and shape
 * <em>are</em> the message schema consumers (the future
 * {@code notification-service}, the NiFi flow) will see — kept as its own
 * type, distinct from {@link StockPriceUpdateRequest}, rather than reusing
 * the request record, so the two can evolve independently once
 * {@code classification} (and later a routing key derived from it) join this
 * event in the Data Classification milestone.
 *
 * @param symbol    ticker symbol this tick belongs to
 * @param timestamp when the tick occurred
 * @param price     the tick's price
 * @param volume    shares traded at this tick
 */
public record StockPriceUpdate(String symbol, Instant timestamp, BigDecimal price, Integer volume) {
}
