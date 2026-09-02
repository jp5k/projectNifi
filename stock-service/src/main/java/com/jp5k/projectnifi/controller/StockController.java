package com.jp5k.projectnifi.controller;

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
 * Basic CRUD REST endpoints for {@link Stock}.
 *
 * <p>Entity is accepted/returned directly for now — no request/response DTOs
 * yet (see the "Solidify the basics" milestone), and no input validation or
 * centralized exception handling yet either, so a malformed body currently
 * surfaces as Spring's default error response rather than a curated one.
 */
@RestController
@RequestMapping("/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    /** Lists every {@link Stock}. */
    @GetMapping
    public List<Stock> findAll() {
        return stockService.findAll();
    }

    /** Fetches a single {@link Stock} by symbol, or 404 if it doesn't exist. */
    @GetMapping("/{symbol}")
    public ResponseEntity<Stock> findBySymbol(@PathVariable String symbol) {
        return stockService.findBySymbol(symbol)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /** Creates a new {@link Stock}. */
    @PostMapping
    public ResponseEntity<Stock> create(@RequestBody Stock stock) {
        Stock saved = stockService.save(stock);
        return ResponseEntity.status(201).body(saved);
    }

    /**
     * Fully replaces the {@link Stock} at {@code symbol} with {@code stock},
     * or 404 if it doesn't exist yet — this endpoint updates, it doesn't
     * upsert.
     */
    @PutMapping("/{symbol}")
    public ResponseEntity<Stock> update(@PathVariable String symbol, @RequestBody Stock stock) {
        if (stockService.findBySymbol(symbol).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Stock updated = new Stock(symbol, stock.getName(), stock.getSector(), stock.getBasePrice());
        return ResponseEntity.ok(stockService.save(updated));
    }

    /** Deletes the {@link Stock} at {@code symbol}, or 404 if it doesn't exist. */
    @DeleteMapping("/{symbol}")
    public ResponseEntity<Void> delete(@PathVariable String symbol) {
        boolean deleted = stockService.deleteBySymbol(symbol);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
