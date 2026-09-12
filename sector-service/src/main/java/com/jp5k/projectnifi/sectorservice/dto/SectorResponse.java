package com.jp5k.projectnifi.sectorservice.dto;

/**
 * Outgoing shape for a {@code Sector} returned by the REST API.
 *
 * <p>Separate from the {@code Sector} JPA entity for the same reason as
 * {@link SectorRequest} — the API's read contract is a deliberate choice, not
 * "whatever columns the table happens to have".
 *
 * @param name        sector name
 * @param description optional free-text description, may be {@code null}
 */
public record SectorResponse(String name, String description) {
}
