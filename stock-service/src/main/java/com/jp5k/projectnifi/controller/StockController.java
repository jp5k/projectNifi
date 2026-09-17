package com.jp5k.projectnifi.controller;

import com.jp5k.projectnifi.dto.StockMapper;
import com.jp5k.projectnifi.dto.StockPriceUpdateRequest;
import com.jp5k.projectnifi.dto.StockRequest;
import com.jp5k.projectnifi.dto.StockResponse;
import com.jp5k.projectnifi.exception.StockNotFoundException;
import com.jp5k.projectnifi.model.Stock;
import com.jp5k.projectnifi.service.StockService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Basic CRUD REST endpoints for {@code Stock}, plus publishing simulated
 * price-update events onto RabbitMQ ({@link #publishPriceUpdate}).
 *
 * <p>The API speaks in DTOs — {@link StockRequest} in, {@link StockResponse}
 * out — never the {@link Stock} JPA entity directly, so the HTTP contract and
 * the persistence model can evolve independently. Conversion is delegated to
 * {@link StockMapper}; this class stays focused on HTTP concerns (routing,
 * status codes).
 *
 * <p>Errors are not handled here. Request bodies are constraint-checked via
 * {@code @Valid} against {@link StockRequest}, and a missing stock is reported
 * by throwing {@link StockNotFoundException}; both — plus anything unexpected —
 * are turned into a consistent {@code application/problem+json} response by
 * {@code GlobalExceptionHandler}. That lets the methods below read as the
 * happy path only.
 */
@RestController
@RequestMapping("/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    /**
     * Lists stocks, one page at a time. Accepts the standard Spring Data
     * {@code page}/{@code size}/{@code sort} query parameters (e.g.
     * {@code ?page=1&size=5&sort=basePrice,desc}); defaults to page 0, size
     * 20, sorted by {@code symbol} ascending when the caller doesn't specify.
     */
    @GetMapping
    public PagedModel<StockResponse> findAll(
            @PageableDefault(size = 20, sort = "symbol") Pageable pageable) {
        return new PagedModel<>(stockService.findAll(pageable).map(StockMapper::toResponse));
    }

    /**
     * Fetches a single stock by symbol.
     *
     * @throws StockNotFoundException if no stock has that symbol (→ 404)
     */
    @GetMapping("/{symbol}")
    public StockResponse findBySymbol(@PathVariable String symbol) {
        return stockService.findBySymbol(symbol)
                .map(StockMapper::toResponse)
                .orElseThrow(() -> new StockNotFoundException(symbol));
    }

    /**
     * Creates a new stock from the request body's fields, symbol included.
     * A body that fails {@link StockRequest}'s constraints is rejected with
     * {@code 400 Bad Request} before this method runs.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StockResponse create(@Valid @RequestBody StockRequest request) {
        Stock saved = stockService.save(StockMapper.toEntity(request));
        return StockMapper.toResponse(saved);
    }

    /**
     * Fully replaces the stock at {@code symbol} with the request body — this
     * endpoint updates, it doesn't upsert. The path {@code symbol} is
     * authoritative; any {@code symbol} in the body is ignored (see
     * {@link StockMapper#toEntity(String, StockRequest)}) but is still
     * constraint-checked, so the body must carry a non-blank {@code symbol}.
     *
     * <p>A body that fails validation is a {@code 400}; that check runs before
     * the existence check, so an invalid body for a missing symbol is still a
     * 400, not a 404.
     *
     * @throws StockNotFoundException if no stock has that symbol (→ 404)
     */
    @PutMapping("/{symbol}")
    public StockResponse update(
            @PathVariable String symbol, @Valid @RequestBody StockRequest request) {
        if (stockService.findBySymbol(symbol).isEmpty()) {
            throw new StockNotFoundException(symbol);
        }
        Stock updated = stockService.save(StockMapper.toEntity(symbol, request));
        return StockMapper.toResponse(updated);
    }

    /**
     * Deletes the stock at {@code symbol}.
     *
     * @throws StockNotFoundException if no stock has that symbol (→ 404)
     */
    @DeleteMapping("/{symbol}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String symbol) {
        if (!stockService.deleteBySymbol(symbol)) {
            throw new StockNotFoundException(symbol);
        }
    }

    /**
     * Publishes a simulated price tick for the stock at {@code symbol} onto
     * RabbitMQ. {@code 202 Accepted} rather than {@code 200}/{@code 201}:
     * this doesn't create or return a persisted resource, it hands a message
     * off for asynchronous processing by whatever consumes the queue.
     *
     * @throws StockNotFoundException if no stock has that symbol (→ 404)
     */
    @PostMapping("/{symbol}/price-updates")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void publishPriceUpdate(
            @PathVariable String symbol, @Valid @RequestBody StockPriceUpdateRequest request) {
        stockService.publishPriceUpdate(StockMapper.toPriceUpdate(symbol, request));
    }
}
