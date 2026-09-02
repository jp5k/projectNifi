package com.jp5k.projectnifi.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Stock}'s identity behaviour (equality is based
 * solely on {@code symbol}).
 */
class StockTests {

    @Test
    void stocksWithSameSymbolAreEqual() {
        Stock a = new Stock("NVTD", "NovaTech Dynamics", "Technology", new BigDecimal("142.50"));
        Stock b = new Stock("NVTD", "A Different Name", "Energy", new BigDecimal("1.00"));

        assertThat(a).isEqualTo(b);
        assertThat(a).hasSameHashCodeAs(b);
    }

    @Test
    void stocksWithDifferentSymbolAreNotEqual() {
        Stock a = new Stock("NVTD", "NovaTech Dynamics", "Technology", new BigDecimal("142.50"));
        Stock b = new Stock("BLHE", "BlueHarbor Energy", "Energy", new BigDecimal("58.20"));

        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void stockIsNotEqualToNullOrOtherType() {
        Stock a = new Stock("NVTD", "NovaTech Dynamics", "Technology", new BigDecimal("142.50"));

        assertThat(a).isNotEqualTo(null);
        assertThat(a).isNotEqualTo("NVTD");
        assertThat(a).isEqualTo(a);
    }

    @Test
    void toStringIncludesAllFields() {
        Stock a = new Stock("NVTD", "NovaTech Dynamics", "Technology", new BigDecimal("142.50"));

        assertThat(a.toString())
                .contains("NVTD", "NovaTech Dynamics", "Technology", "142.50");
    }
}
