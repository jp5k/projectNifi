package com.jp5k.projectnifi.sectorservice.dto;

import com.jp5k.projectnifi.sectorservice.model.Sector;

/**
 * Converts between the {@code Sector} JPA entity and the REST DTOs
 * ({@link SectorRequest}, {@link SectorResponse}).
 *
 * <p>Implemented as static methods on a non-instantiable class, mirroring
 * {@code stock-service}'s {@code StockMapper}: the conversion is pure, so
 * there's nothing to inject and nothing to mock.
 */
public final class SectorMapper {

    /** Not instantiable — this is a namespace for the static mappers below. */
    private SectorMapper() {
    }

    /**
     * Builds a new {@link Sector} from a create request, taking the
     * identifier from the request body's {@code name}.
     *
     * <p>Used by {@code POST /sectors}, where the client chooses the name.
     *
     * @param request the incoming request body
     * @return a transient {@link Sector} ready to be persisted
     */
    public static Sector toEntity(SectorRequest request) {
        return new Sector(request.name(), request.description());
    }

    /**
     * Builds a new {@link Sector} from a replace request, taking the
     * identifier from the URL rather than the body.
     *
     * <p>Used by {@code PUT /sectors/{name}}: the path segment is the single
     * source of truth for <em>which</em> sector is being replaced, so any
     * {@code name} in the body is ignored.
     *
     * @param name    the identifier from the request path
     * @param request the incoming request body (its {@code name} is ignored)
     * @return a {@link Sector} carrying {@code name} and the body's other fields
     */
    public static Sector toEntity(String name, SectorRequest request) {
        return new Sector(name, request.description());
    }

    /**
     * Projects a persisted {@link Sector} onto the API's read contract.
     *
     * @param sector the entity to expose
     * @return the response DTO to serialize back to the client
     */
    public static SectorResponse toResponse(Sector sector) {
        return new SectorResponse(sector.getName(), sector.getDescription());
    }
}
