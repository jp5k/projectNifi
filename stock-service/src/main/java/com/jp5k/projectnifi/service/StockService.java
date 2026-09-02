package com.jp5k.projectnifi.service;

import com.jp5k.projectnifi.model.Stock;
import com.jp5k.projectnifi.repository.StockRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Business logic for {@link Stock}.
 *
 * <p>Currently a thin pass-through to {@link StockRepository} — basic CRUD
 * only, entity in and out directly (no DTOs yet, see the "Solidify the
 * basics" milestone). Kept as its own layer regardless, so the controller
 * never talks to the repository directly, and so this is the natural place
 * to add rules later (e.g. classification filtering, sector validation via
 * {@code sector-service}).
 */
@Service
public class StockService {

    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    /** Returns every {@link Stock}, in no particular order. */
    public List<Stock> findAll() {
        return stockRepository.findAll();
    }

    /** Returns the {@link Stock} with the given symbol, if it exists. */
    public Optional<Stock> findBySymbol(String symbol) {
        return stockRepository.findById(symbol);
    }

    /** Creates or fully replaces the {@link Stock} with the given symbol. */
    public Stock save(Stock stock) {
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
}
