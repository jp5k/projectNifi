package com.jp5k.projectnifi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A fictional company tracked by {@code stock-service}, identified by its
 * ticker {@code symbol}.
 *
 * <p>This is reference/master data — see {@code sample-data/stocks.json} for
 * the seed set this entity is shaped to match. Fields deliberately kept
 * minimal for now: {@code classification} (PUBLIC/INTERNAL/RESTRICTED) is
 * added later, in the Data Classification & Access Control milestone, and
 * {@code sector} stays a plain string here rather than a relation until the
 * Sector Service milestone gives it something to reference.
 */
@Entity
@Table(name = "stocks")
public class Stock {

    /**
     * Ticker symbol, e.g. {@code "NVTD"}. Used as the primary key rather than
     * a generated ID, since it's already a natural, stable unique identifier
     * for a stock.
     */
    @Id
    @Column(nullable = false, updatable = false)
    private String symbol;

    /** Full company name, e.g. {@code "NovaTech Dynamics"}. */
    @Column(nullable = false)
    private String name;

    /**
     * Sector name, e.g. {@code "Technology"}. A plain string for now — no
     * validation against {@code sector-service} yet, that arrives with the
     * Microservices: Sector Service milestone.
     */
    @Column(nullable = false)
    private String sector;

    /** Reference price this stock's simulated ticks are generated around. */
    @Column(nullable = false)
    private BigDecimal basePrice;

    /** No-args constructor required by JPA; not for application use. */
    protected Stock() {
    }

    /**
     * Creates a new {@code Stock}.
     *
     * @param symbol    ticker symbol, used as the entity's identifier
     * @param name      full company name
     * @param sector    sector name
     * @param basePrice reference price
     */
    public Stock(String symbol, String name, String sector, BigDecimal basePrice) {
        this.symbol = symbol;
        this.name = name;
        this.sector = sector;
        this.basePrice = basePrice;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public String getSector() {
        return sector;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    /**
     * Equality is based solely on {@code symbol}, consistent with it being
     * this entity's identifier.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Stock stock)) {
            return false;
        }
        return Objects.equals(symbol, stock.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol);
    }

    @Override
    public String toString() {
        return "Stock{symbol='%s', name='%s', sector='%s', basePrice=%s}"
                .formatted(symbol, name, sector, basePrice);
    }
}
