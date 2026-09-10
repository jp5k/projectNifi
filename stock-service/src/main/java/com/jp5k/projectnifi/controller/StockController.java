package com.jp5k.projectnifi.controller;

import com.jp5k.projectnifi.dto.StockMapper;
import com.jp5k.projectnifi.dto.StockRequest;
import com.jp5k.projectnifi.dto.StockResponse;
import com.jp5k.projectnifi.model.Stock;
import com.jp5k.projectnifi.service.StockService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Basic CRUD REST endpoints for {@code Stock}.
 *
 * <p>The API speaks in DTOs — {@link StockRequest} in, {@link StockResponse}
 * out — never the {@link Stock} JPA entity directly, so the HTTP contract and
 * the persistence model can evolve independently. Conversion is delegated to
 * {@link StockMapper}; this class stays focused on HTTP concerns (routing,
 * status codes).
 *
 * <p>No input validation or centralized exception handling yet (both are their
 * own upcoming roadmap items), so a malformed body still surfaces as Spring's
 * default error response rather than a curated one, and not-found cases return
 * a plain 404.
 */
@RestController
@RequestMapping("/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    /** Lists every stock. */
    @GetMapping
    public List<StockResponse> findAll() {
        return stockService.findAll().stream()
                .map(StockMapper::toResponse)
                .toList();
    }

    /** Fetches a single stock by symbol, or 404 if it doesn't exist. */
    @GetMapping("/{symbol}")
    public ResponseEntity<StockResponse> findBySymbol(@PathVariable String symbol) {
        return stockService.findBySymbol(symbol)
                .map(StockMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /** Creates a new stock from the request body's fields, symbol included. */
    @PostMapping
    public ResponseEntity<StockResponse> create(@RequestBody StockRequest request) {
        Stock saved = stockService.save(StockMapper.toEntity(request));
        return ResponseEntity.status(201).body(StockMapper.toResponse(saved));
    }

    /**
     * Fully replaces the stock at {@code symbol} with the request body, or 404
     * if it doesn't exist yet — this endpoint updates, it doesn't upsert. The
     * path {@code symbol} is authoritative; any {@code symbol} in the body is
     * ignored (see {@link StockMapper#toEntity(String, StockRequest)}).
     */
    @PutMapping("/{symbol}")
    public ResponseEntity<StockResponse> update(@PathVariable String symbol, @RequestBody StockRequest request) {
        if (stockService.findBySymbol(symbol).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Stock updated = stockService.save(StockMapper.toEntity(symbol, request));
        return ResponseEntity.ok(StockMapper.toResponse(updated));
    }

    /** Deletes the stock at {@code symbol}, or 404 if it doesn't exist. */
    @DeleteMapping("/{symbol}")
    public ResponseEntity<Void> delete(@PathVariable String symbol) {
        boolean deleted = stockService.deleteBySymbol(symbol);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
