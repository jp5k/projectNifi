package com.jp5k.projectnifi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jp5k.projectnifi.dto.StockPriceUpdate;
import com.jp5k.projectnifi.exception.StockNotFoundException;
import com.jp5k.projectnifi.exception.UnknownSectorException;
import com.jp5k.projectnifi.model.Stock;
import com.jp5k.projectnifi.repository.StockRepository;
import java.math.BigDecimal;
import java.time.Instant;
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
 * Unit tests for {@link StockService}, with {@link StockRepository} and
 * {@link SectorClient} mocked.
 */
@ExtendWith(MockitoExtension.class)
class StockServiceTests {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private SectorClient sectorClient;

    @Mock
    private StockPriceUpdatePublisher stockPriceUpdatePublisher;

    private StockService stockService;

    private final Stock stock = new Stock("NVTD", "NovaTech Dynamics", "Technology", new BigDecimal("142.50"));

    @BeforeEach
    void setUp() {
        stockService = new StockService(stockRepository, sectorClient, stockPriceUpdatePublisher);
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
    void saveDelegatesToRepositoryWhenSectorExists() {
        when(sectorClient.exists("Technology")).thenReturn(true);
        when(stockRepository.save(stock)).thenReturn(stock);

        assertThat(stockService.save(stock)).isEqualTo(stock);
    }

    @Test
    void saveThrowsUnknownSectorExceptionWhenSectorDoesNotExistAndNeverPersists() {
        when(sectorClient.exists("Technology")).thenReturn(false);

        assertThatThrownBy(() -> stockService.save(stock))
                .isInstanceOf(UnknownSectorException.class)
                .hasMessage("No sector found with name 'Technology'");
        verify(stockRepository, never()).save(stock);
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

    @Test
    void publishPriceUpdateDelegatesToPublisherWhenStockExists() {
        StockPriceUpdate update =
                new StockPriceUpdate("NVTD", Instant.parse("2026-08-11T09:30:00Z"), new BigDecimal("142.50"), 1200);
        when(stockRepository.existsById("NVTD")).thenReturn(true);

        stockService.publishPriceUpdate(update);

        verify(stockPriceUpdatePublisher).publish(update);
    }

    @Test
    void publishPriceUpdateThrowsStockNotFoundExceptionWhenStockDoesNotExistAndNeverPublishes() {
        StockPriceUpdate update =
                new StockPriceUpdate("UNKNOWN", Instant.parse("2026-08-11T09:30:00Z"), new BigDecimal("142.50"), 1200);
        when(stockRepository.existsById("UNKNOWN")).thenReturn(false);

        assertThatThrownBy(() -> stockService.publishPriceUpdate(update))
                .isInstanceOf(StockNotFoundException.class);
        verify(stockPriceUpdatePublisher, never()).publish(update);
    }
}
