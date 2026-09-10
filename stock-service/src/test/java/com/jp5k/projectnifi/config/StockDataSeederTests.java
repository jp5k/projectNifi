package com.jp5k.projectnifi.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jp5k.projectnifi.model.Stock;
import com.jp5k.projectnifi.repository.StockRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link StockDataSeeder}, exercising the {@code CommandLineRunner}
 * it produces directly (no Spring context) with a mocked {@link StockRepository}.
 *
 * <p>The real {@code sample-data/stocks.json} is on the test classpath (the
 * build copies it there — see {@code stock-service/pom.xml}), so these run
 * against the actual seed file rather than a fixture: if its shape drifts from
 * {@link Stock}, {@link #seedsEveryStockFromTheSampleFileWhenTableIsEmpty()}
 * fails.
 */
@ExtendWith(MockitoExtension.class)
class StockDataSeederTests {

    @Mock
    private StockRepository stockRepository;

    @Captor
    private ArgumentCaptor<List<Stock>> savedStocksCaptor;

    private final StockDataSeeder seeder = new StockDataSeeder();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void seedsEveryStockFromTheSampleFileWhenTableIsEmpty() throws Exception {
        when(stockRepository.count()).thenReturn(0L);

        seeder.seedStocks(stockRepository, objectMapper).run();

        verify(stockRepository).saveAll(savedStocksCaptor.capture());
        assertThat(savedStocksCaptor.getValue())
                .hasSize(10)
                .extracting(Stock::getSymbol)
                .contains("NVTD", "BLHE", "EMBR");
        assertThat(savedStocksCaptor.getValue()).allSatisfy(stock -> {
            assertThat(stock.getName()).isNotBlank();
            assertThat(stock.getSector()).isNotBlank();
            assertThat(stock.getBasePrice()).isPositive();
        });
    }

    @Test
    void skipsSeedWhenTableAlreadyHasData() throws Exception {
        when(stockRepository.count()).thenReturn(5L);

        seeder.seedStocks(stockRepository, objectMapper).run();

        verify(stockRepository).count();
        verify(stockRepository, never()).saveAll(any());
    }
}
