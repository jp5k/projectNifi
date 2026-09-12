package com.jp5k.projectnifi.sectorservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

/**
 * A sector that {@code Stock}s can be grouped under, e.g. {@code "Technology"}
 * or {@code "Healthcare"}.
 *
 * <p>Reference/master data, owned by {@code sector-service} — {@code
 * stock-service} calls this service synchronously to validate a Stock's
 * sector rather than duplicating this table. Deliberately minimal: a name and
 * an optional description are all a sector needs to be looked up and
 * validated against.
 */
@Entity
@Table(name = "sectors")
public class Sector {

    /**
     * Sector name, e.g. {@code "Technology"}. Used as the primary key rather
     * than a generated ID, since it's already a natural, stable unique
     * identifier — the same choice made for {@code Stock#symbol} in
     * {@code stock-service}.
     */
    @Id
    @Column(nullable = false, updatable = false)
    private String name;

    /**
     * Optional free-text description of the sector. Nullable — a sector is
     * fully identified by its {@link #name} alone; the description exists
     * only to give humans more context and to give {@code PUT} something
     * meaningful to change.
     */
    @Column
    private String description;

    /** No-args constructor required by JPA; not for application use. */
    protected Sector() {
    }

    /**
     * Creates a new {@code Sector}.
     *
     * @param name        sector name, used as the entity's identifier
     * @param description optional free-text description, may be {@code null}
     */
    public Sector(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Equality is based solely on {@code name}, consistent with it being
     * this entity's identifier.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Sector sector)) {
            return false;
        }
        return Objects.equals(name, sector.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Sector{name='%s', description='%s'}".formatted(name, description);
    }
}
