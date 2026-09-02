package com.jp5k.projectnifi.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.jp5k.projectnifi.model.Stock;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * Verifies {@link StockRepository} persists and retrieves {@link Stock}
 * entities correctly against the H2 test database.
 */
@DataJpaTest
class StockRepositoryTests {

    @Autowired
    private StockRepository stockRepository;

    @Test
    void savesAndFindsStockBySymbol() {
        Stock stock = new Stock("NVTD", "NovaTech Dynamics", "Technology", new BigDecimal("142.50"));

        stockRepository.save(stock);

        Optional<Stock> found = stockRepository.findById("NVTD");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("NovaTech Dynamics");
        assertThat(found.get().getSector()).isEqualTo("Technology");
        assertThat(found.get().getBasePrice()).isEqualByComparingTo("142.50");
    }
}
