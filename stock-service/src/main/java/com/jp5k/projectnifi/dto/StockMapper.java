package com.jp5k.projectnifi.dto;

import com.jp5k.projectnifi.model.Stock;

/**
 * Converts between the {@code Stock} JPA entity and the REST DTOs
 * ({@link StockRequest}, {@link StockResponse}).
 *
 * <p>The mapping lives in one place, rather than being scattered across the
 * controller, so there is a single obvious spot to adjust when the entity and
 * the API contract drift apart (which they will — {@code classification} is the
 * first case coming). Keeping it out of the controller also keeps the
 * controller focused on HTTP concerns (status codes, routing) instead of field
 * copying.
 *
 * <p>Implemented as static methods on a non-instantiable class: the conversion
 * is pure (no state, no collaborators), so there is nothing to inject and
 * nothing to mock — callers and tests can just use it directly. If a mapping
 * ever needs a dependency (say, a lookup service), this becomes a Spring
 * {@code @Component} at that point.
 */
public final class StockMapper {

    /** Not instantiable — this is a namespace for the static mappers below. */
    private StockMapper() {
    }

    /**
     * Builds a new {@link Stock} from a create request, taking the identifier
     * from the request body's {@code symbol}.
     *
     * <p>Used by {@code POST /stocks}, where the client chooses the symbol.
     *
     * @param request the incoming request body
     * @return a transient {@link Stock} ready to be persisted
     */
    public static Stock toEntity(StockRequest request) {
        return new Stock(request.symbol(), request.name(), request.sector(), request.basePrice());
    }

    /**
     * Builds a new {@link Stock} from a replace request, taking the identifier
     * from the URL rather than the body.
     *
     * <p>Used by {@code PUT /stocks/{symbol}}: the path segment is the single
     * source of truth for <em>which</em> stock is being replaced, so any
     * {@code symbol} in the body is ignored. This prevents a request to
     * {@code PUT /stocks/AAAA} with a body symbol of {@code BBBB} from writing
     * to the wrong row.
     *
     * @param symbol  the identifier from the request path
     * @param request the incoming request body (its {@code symbol} is ignored)
     * @return a {@link Stock} carrying {@code symbol} and the body's other fields
     */
    public static Stock toEntity(String symbol, StockRequest request) {
        return new Stock(symbol, request.name(), request.sector(), request.basePrice());
    }

    /**
     * Projects a persisted {@link Stock} onto the API's read contract.
     *
     * @param stock the entity to expose
     * @return the response DTO to serialize back to the client
     */
    public static StockResponse toResponse(Stock stock) {
        return new StockResponse(stock.getSymbol(), stock.getName(), stock.getSector(), stock.getBasePrice());
    }

    /**
     * Builds the {@link StockPriceUpdate} event to publish from a price-update
     * request, taking the symbol from the URL path — same rationale as
     * {@link #toEntity(String, StockRequest)}.
     *
     * @param symbol  the identifier from the request path
     * @param request the incoming request body
     * @return the event to publish onto RabbitMQ
     */
    public static StockPriceUpdate toPriceUpdate(String symbol, StockPriceUpdateRequest request) {
        return new StockPriceUpdate(symbol, request.timestamp(), request.price(), request.volume());
    }
}
