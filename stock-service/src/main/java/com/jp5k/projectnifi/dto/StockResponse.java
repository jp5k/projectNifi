package com.jp5k.projectnifi.dto;

import com.jp5k.projectnifi.model.Stock;
import java.math.BigDecimal;

/**
 * Outgoing shape for a {@code Stock} returned by the REST API.
 *
 * <p>Separate from the {@code Stock} JPA entity for the same reason as
 * {@link StockRequest}: what we expose to clients is a deliberate choice, not
 * "whatever columns the table happens to have". Returning the entity directly
 * would leak persistence details (and, once relationships exist, risk lazy-load
 * exceptions or accidental deep serialization), and would tie the JSON clients
 * depend on to the database schema. This record is the API's read contract —
 * fields are added here only when we mean to publish them. Today it mirrors the
 * entity one-to-one; that will stop being true once {@code classification}
 * lands and some callers are filtered from some fields.
 *
 * @param symbol    ticker symbol
 * @param name      full company name
 * @param sector    sector name
 * @param basePrice reference price
 */
public record StockResponse(String symbol, String name, String sector, BigDecimal basePrice) {
}
