package com.jp5k.projectnifi.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.jp5k.projectnifi.model.Stock;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link StockMapper}, covering both entity-building overloads
 * and the entity-to-response projection.
 */
class StockMapperTests {

    private final StockRequest request =
            new StockRequest("NVTD", "NovaTech Dynamics", "Technology", new BigDecimal("142.50"));

    @Test
    void toEntityFromRequestCopiesEveryFieldIncludingSymbol() {
        Stock entity = StockMapper.toEntity(request);

        assertThat(entity.getSymbol()).isEqualTo("NVTD");
        assertThat(entity.getName()).isEqualTo("NovaTech Dynamics");
        assertThat(entity.getSector()).isEqualTo("Technology");
        assertThat(entity.getBasePrice()).isEqualByComparingTo("142.50");
    }

    @Test
    void toEntityWithSymbolTakesTheSymbolFromTheArgumentNotTheBody() {
        StockRequest bodyWithDifferentSymbol =
                new StockRequest("WRONG", "NovaTech Dynamics", "Technology", new BigDecimal("142.50"));

        Stock entity = StockMapper.toEntity("NVTD", bodyWithDifferentSymbol);

        assertThat(entity.getSymbol()).isEqualTo("NVTD");
        assertThat(entity.getName()).isEqualTo("NovaTech Dynamics");
        assertThat(entity.getSector()).isEqualTo("Technology");
        assertThat(entity.getBasePrice()).isEqualByComparingTo("142.50");
    }

    @Test
    void toResponseProjectsEveryEntityField() {
        Stock entity = new Stock("NVTD", "NovaTech Dynamics", "Technology", new BigDecimal("142.50"));

        StockResponse response = StockMapper.toResponse(entity);

        assertThat(response).isEqualTo(
                new StockResponse("NVTD", "NovaTech Dynamics", "Technology", new BigDecimal("142.50")));
    }
}
