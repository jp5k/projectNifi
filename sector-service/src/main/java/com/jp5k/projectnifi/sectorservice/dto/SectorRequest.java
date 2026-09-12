package com.jp5k.projectnifi.sectorservice.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Incoming shape for creating or replacing a {@code Sector} over the REST API.
 *
 * <p>Kept separate from the {@code Sector} JPA entity for the same reason
 * {@code stock-service}'s {@code StockRequest} is kept separate from
 * {@code Stock}: the request body is a contract with API clients, whereas the
 * entity is a persistence concern.
 *
 * @param name        sector name, e.g. {@code "Technology"}. Must be
 *                    non-blank on create ({@code POST /sectors}), where it
 *                    becomes the identifier. On replace
 *                    ({@code PUT /sectors/{name}}) the path segment is
 *                    authoritative and the body value is ignored by the
 *                    mapper — but it is still validated here, so a
 *                    {@code PUT} body must carry a non-blank {@code name}
 *                    too (send the same name as the path).
 * @param description optional free-text description; no constraint, since a
 *                     sector is fully identified by {@code name} alone.
 */
public record SectorRequest(@NotBlank String name, String description) {
}
