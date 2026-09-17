package com.jp5k.projectnifi.service;

import com.jp5k.projectnifi.dto.StockPriceUpdate;
import com.jp5k.projectnifi.exception.StockNotFoundException;
import com.jp5k.projectnifi.exception.UnknownSectorException;
import com.jp5k.projectnifi.model.Stock;
import com.jp5k.projectnifi.repository.StockRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Business logic for {@link Stock}.
 *
 * <p>Mostly a thin pass-through to {@link StockRepository} — basic CRUD, plus
 * validating a stock's sector against {@code sector-service} on write (see
 * {@link #save}) and publishing price-update events onto RabbitMQ (see
 * {@link #publishPriceUpdate}). Works in terms of the {@link Stock} entity,
 * not the REST DTOs: the controller owns the HTTP contract and maps to/from
 * DTOs, leaving this layer free to be reused by non-HTTP callers later. Kept
 * as its own layer regardless, so the controller never talks to the
 * repository directly, and so this is the natural place to add further rules
 * later (e.g. classification filtering).
 *
 * <p>Note: {@code StockDataSeeder} deliberately bypasses this class and writes
 * to {@link StockRepository} directly, so startup seeding doesn't depend on
 * {@code sector-service} being up.
 */
@Service
public class StockService {

    private final StockRepository stockRepository;
    private final SectorClient sectorClient;
    private final StockPriceUpdatePublisher stockPriceUpdatePublisher;

    public StockService(
            StockRepository stockRepository,
            SectorClient sectorClient,
            StockPriceUpdatePublisher stockPriceUpdatePublisher) {
        this.stockRepository = stockRepository;
        this.sectorClient = sectorClient;
        this.stockPriceUpdatePublisher = stockPriceUpdatePublisher;
    }

    /**
     * Returns one page of {@link Stock}, ordered and sliced as described by
     * {@code pageable} — the controller is responsible for supplying sensible
     * defaults (page size, sort) from the incoming request.
     */
    public Page<Stock> findAll(Pageable pageable) {
        return stockRepository.findAll(pageable);
    }

    /** Returns the {@link Stock} with the given symbol, if it exists. */
    public Optional<Stock> findBySymbol(String symbol) {
        return stockRepository.findById(symbol);
    }

    /**
     * Creates or fully replaces the {@link Stock} with the given symbol,
     * after validating (synchronously, via {@link SectorClient}) that its
     * sector exists in {@code sector-service}.
     *
     * @throws UnknownSectorException if {@code sector-service} doesn't
     *     recognize {@code stock.getSector()}
     */
    public Stock save(Stock stock) {
        if (!sectorClient.exists(stock.getSector())) {
            throw new UnknownSectorException(stock.getSector());
        }
        return stockRepository.save(stock);
    }

    /**
     * Deletes the {@link Stock} with the given symbol.
     *
     * @return {@code true} if a stock with that symbol existed and was
     *     deleted, {@code false} if there was nothing to delete
     */
    public boolean deleteBySymbol(String symbol) {
        if (!stockRepository.existsById(symbol)) {
            return false;
        }
        stockRepository.deleteById(symbol);
        return true;
    }

    /**
     * Publishes {@code update} onto RabbitMQ via {@link StockPriceUpdatePublisher},
     * after confirming a stock with its symbol exists — a tick for an unknown
     * stock is bad input, same principle as {@link UnknownSectorException} for
     * an unknown sector on write.
     *
     * @throws StockNotFoundException if no stock has {@code update}'s symbol
     */
    public void publishPriceUpdate(StockPriceUpdate update) {
        if (!stockRepository.existsById(update.symbol())) {
            throw new StockNotFoundException(update.symbol());
        }
        stockPriceUpdatePublisher.publish(update);
    }
}
