package com.jp5k.projectnifi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jp5k.projectnifi.model.Stock;
import com.jp5k.projectnifi.service.StockService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for {@link StockController}, exercising the full
 * request/response cycle through {@link MockMvc} with {@link StockService}
 * mocked out.
 */
@WebMvcTest(StockController.class)
class StockControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StockService stockService;

    private final Stock stock = new Stock("NVTD", "NovaTech Dynamics", "Technology", new BigDecimal("142.50"));

    @Test
    void findAllReturnsEveryStock() throws Exception {
        when(stockService.findAll()).thenReturn(List.of(stock));

        mockMvc.perform(get("/stocks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].symbol").value("NVTD"));
    }

    @Test
    void findBySymbolReturnsStockWhenPresent() throws Exception {
        when(stockService.findBySymbol("NVTD")).thenReturn(Optional.of(stock));

        mockMvc.perform(get("/stocks/NVTD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NovaTech Dynamics"));
    }

    @Test
    void findBySymbolReturns404WhenMissing() throws Exception {
        when(stockService.findBySymbol("UNKNOWN")).thenReturn(Optional.empty());

        mockMvc.perform(get("/stocks/UNKNOWN")).andExpect(status().isNotFound());
    }

    @Test
    void createReturns201WithCreatedStock() throws Exception {
        when(stockService.save(any(Stock.class))).thenReturn(stock);

        mockMvc.perform(post("/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"symbol":"NVTD","name":"NovaTech Dynamics","sector":"Technology","basePrice":142.50}
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.symbol").value("NVTD"));
    }

    @Test
    void updateReturnsUpdatedStockWhenPresent() throws Exception {
        Stock updated = new Stock("NVTD", "NovaTech Dynamics Inc.", "Technology", new BigDecimal("150.00"));
        when(stockService.findBySymbol("NVTD")).thenReturn(Optional.of(stock));
        when(stockService.save(any(Stock.class))).thenReturn(updated);

        mockMvc.perform(put("/stocks/NVTD")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"symbol":"NVTD","name":"NovaTech Dynamics Inc.","sector":"Technology","basePrice":150.00}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NovaTech Dynamics Inc."));
    }

    @Test
    void updateReturns404WhenMissing() throws Exception {
        when(stockService.findBySymbol("UNKNOWN")).thenReturn(Optional.empty());

        mockMvc.perform(put("/stocks/UNKNOWN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"symbol":"UNKNOWN","name":"Doesn't matter","sector":"Technology","basePrice":1.00}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteReturns204WhenPresent() throws Exception {
        when(stockService.deleteBySymbol("NVTD")).thenReturn(true);

        mockMvc.perform(delete("/stocks/NVTD")).andExpect(status().isNoContent());
        verify(stockService).deleteBySymbol(eq("NVTD"));
    }

    @Test
    void deleteReturns404WhenMissing() throws Exception {
        when(stockService.deleteBySymbol("UNKNOWN")).thenReturn(false);

        mockMvc.perform(delete("/stocks/UNKNOWN")).andExpect(status().isNotFound());
    }

    @Test
    void createReturns400WhenNameIsBlank() throws Exception {
        mockMvc.perform(post("/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"symbol":"NVTD","name":"  ","sector":"Technology","basePrice":142.50}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(stockService);
    }

    @Test
    void createReturns400WhenSymbolIsMissing() throws Exception {
        mockMvc.perform(post("/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"NovaTech Dynamics","sector":"Technology","basePrice":142.50}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(stockService);
    }

    @Test
    void createReturns400WhenBasePriceIsMissing() throws Exception {
        mockMvc.perform(post("/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"symbol":"NVTD","name":"NovaTech Dynamics","sector":"Technology"}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(stockService);
    }

    @Test
    void createReturns400WhenBasePriceIsNotPositive() throws Exception {
        mockMvc.perform(post("/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"symbol":"NVTD","name":"NovaTech Dynamics","sector":"Technology","basePrice":0}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(stockService);
    }

    @Test
    void updateReturns400OnInvalidBodyWithoutCheckingExistence() throws Exception {
        mockMvc.perform(put("/stocks/NVTD")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"symbol":"NVTD","name":"NovaTech Dynamics","sector":"Technology","basePrice":-5}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(stockService);
    }
}
