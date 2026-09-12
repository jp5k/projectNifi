package com.jp5k.projectnifi.sectorservice.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Sector}'s identity behaviour (equality is based
 * solely on {@code name}).
 */
class SectorTests {

    @Test
    void sectorsWithSameNameAreEqual() {
        Sector a = new Sector("Technology", "Tech companies");
        Sector b = new Sector("Technology", "A different description");

        assertThat(a).isEqualTo(b);
        assertThat(a).hasSameHashCodeAs(b);
    }

    @Test
    void sectorsWithDifferentNameAreNotEqual() {
        Sector a = new Sector("Technology", "Tech companies");
        Sector b = new Sector("Energy", "Energy companies");

        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void sectorIsNotEqualToNullOrOtherType() {
        Sector a = new Sector("Technology", "Tech companies");

        assertThat(a).isNotEqualTo(null);
        assertThat(a).isNotEqualTo("Technology");
        assertThat(a).isEqualTo(a);
    }

    @Test
    void toStringIncludesAllFields() {
        Sector a = new Sector("Technology", "Tech companies");

        assertThat(a.toString()).contains("Technology", "Tech companies");
    }
}
