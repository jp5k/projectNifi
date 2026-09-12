package com.jp5k.projectnifi.sectorservice.controller;

import com.jp5k.projectnifi.sectorservice.dto.SectorMapper;
import com.jp5k.projectnifi.sectorservice.dto.SectorRequest;
import com.jp5k.projectnifi.sectorservice.dto.SectorResponse;
import com.jp5k.projectnifi.sectorservice.exception.SectorNotFoundException;
import com.jp5k.projectnifi.sectorservice.model.Sector;
import com.jp5k.projectnifi.sectorservice.service.SectorService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Basic CRUD REST endpoints for {@code Sector}.
 *
 * <p>The API speaks in DTOs — {@link SectorRequest} in, {@link SectorResponse}
 * out — never the {@link Sector} JPA entity directly, mirroring
 * {@code stock-service}'s {@code StockController}. Conversion is delegated to
 * {@link SectorMapper}; this class stays focused on HTTP concerns.
 *
 * <p>{@code GET /sectors/{name}} is the endpoint {@code stock-service} will
 * call synchronously to validate a Stock's sector, once that milestone lands.
 *
 * <p>Errors are not handled here. Request bodies are constraint-checked via
 * {@code @Valid} against {@link SectorRequest}, and a missing sector is
 * reported by throwing {@link SectorNotFoundException}; both — plus anything
 * unexpected — are turned into a consistent {@code application/problem+json}
 * response by {@code GlobalExceptionHandler}.
 */
@RestController
@RequestMapping("/sectors")
public class SectorController {

    private final SectorService sectorService;

    public SectorController(SectorService sectorService) {
        this.sectorService = sectorService;
    }

    /** Lists every sector. No pagination — small, fixed reference data. */
    @GetMapping
    public List<SectorResponse> findAll() {
        return sectorService.findAll().stream().map(SectorMapper::toResponse).toList();
    }

    /**
     * Fetches a single sector by name.
     *
     * @throws SectorNotFoundException if no sector has that name (→ 404)
     */
    @GetMapping("/{name}")
    public SectorResponse findByName(@PathVariable String name) {
        return sectorService.findByName(name)
                .map(SectorMapper::toResponse)
                .orElseThrow(() -> new SectorNotFoundException(name));
    }

    /**
     * Creates a new sector from the request body's fields, name included.
     * A body that fails {@link SectorRequest}'s constraints is rejected with
     * {@code 400 Bad Request} before this method runs.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SectorResponse create(@Valid @RequestBody SectorRequest request) {
        Sector saved = sectorService.save(SectorMapper.toEntity(request));
        return SectorMapper.toResponse(saved);
    }

    /**
     * Fully replaces the sector at {@code name} with the request body — this
     * endpoint updates, it doesn't upsert. The path {@code name} is
     * authoritative; any {@code name} in the body is ignored (see
     * {@link SectorMapper#toEntity(String, SectorRequest)}) but is still
     * constraint-checked, so the body must carry a non-blank {@code name}.
     *
     * <p>A body that fails validation is a {@code 400}; that check runs before
     * the existence check, so an invalid body for a missing name is still a
     * 400, not a 404.
     *
     * @throws SectorNotFoundException if no sector has that name (→ 404)
     */
    @PutMapping("/{name}")
    public SectorResponse update(
            @PathVariable String name, @Valid @RequestBody SectorRequest request) {
        if (sectorService.findByName(name).isEmpty()) {
            throw new SectorNotFoundException(name);
        }
        Sector updated = sectorService.save(SectorMapper.toEntity(name, request));
        return SectorMapper.toResponse(updated);
    }

    /**
     * Deletes the sector at {@code name}.
     *
     * @throws SectorNotFoundException if no sector has that name (→ 404)
     */
    @DeleteMapping("/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String name) {
        if (!sectorService.deleteByName(name)) {
            throw new SectorNotFoundException(name);
        }
    }
}
