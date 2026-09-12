package com.jp5k.projectnifi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jp5k.projectnifi.model.Stock;
import com.jp5k.projectnifi.repository.StockRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Unit tests for {@link StockService}, with {@link StockRepository} mocked.
 */
@ExtendWith(MockitoExtension.class)
class StockServiceTests {

    @Mock
    private StockRepository stockRepository;

    private StockService stockService;

    private final Stock stock = new Stock("NVTD", "NovaTech Dynamics", "Technology", new BigDecimal("142.50"));

    @BeforeEach
    void setUp() {
        stockService = new StockService(stockRepository);
    }

    @Test
    void findAllReturnsThePageFromTheRepository() {
        Pageable pageable = PageRequest.of(0, 20);
        when(stockRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(stock)));

        assertThat(stockService.findAll(pageable)).containsExactly(stock);
    }

    @Test
    void findBySymbolReturnsStockWhenPresent() {
        when(stockRepository.findById("NVTD")).thenReturn(Optional.of(stock));

        assertThat(stockService.findBySymbol("NVTD")).contains(stock);
    }

    @Test
    void findBySymbolReturnsEmptyWhenMissing() {
        when(stockRepository.findById("UNKNOWN")).thenReturn(Optional.empty());

        assertThat(stockService.findBySymbol("UNKNOWN")).isEmpty();
    }

    @Test
    void saveDelegatesToRepository() {
        when(stockRepository.save(stock)).thenReturn(stock);

        assertThat(stockService.save(stock)).isEqualTo(stock);
    }

    @Test
    void deleteBySymbolDeletesAndReturnsTrueWhenPresent() {
        when(stockRepository.existsById("NVTD")).thenReturn(true);

        assertThat(stockService.deleteBySymbol("NVTD")).isTrue();
        verify(stockRepository).deleteById("NVTD");
    }

    @Test
    void deleteBySymbolReturnsFalseWhenMissing() {
        when(stockRepository.existsById("UNKNOWN")).thenReturn(false);

        assertThat(stockService.deleteBySymbol("UNKNOWN")).isFalse();
    }
}
