/**
 * Request/response DTOs for {@code sector-service}'s REST API.
 *
 * <p>Classes here define the shapes actually sent and received over HTTP,
 * kept separate from the JPA entities in the {@code model} package so the
 * persistence model can evolve independently of the public API contract —
 * including the contract {@code stock-service} depends on when it validates
 * a Stock's sector.
 */
package com.jp5k.projectnifi.sectorservice.dto;
