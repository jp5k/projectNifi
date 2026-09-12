package com.jp5k.projectnifi.sectorservice.exception;

/**
 * Thrown when an operation targets a {@code Sector} name that doesn't exist.
 *
 * <p>The controller throws this instead of hand-assembling a 404
 * {@code ResponseEntity} at each call site: the mapping from "no such sector"
 * to an HTTP 404 (and its response body) lives in one place,
 * {@link GlobalExceptionHandler}. Mirrors {@code stock-service}'s
 * {@code StockNotFoundException}.
 *
 * <p>Extends {@link RuntimeException} (unchecked) deliberately — a missing
 * sector is a client error to be reported, not something most call sites can
 * meaningfully recover from.
 */
public class SectorNotFoundException extends RuntimeException {

    /**
     * @param name the sector name that could not be found; embedded in the
     *     message so logs and the 404 response body say which sector was meant
     */
    public SectorNotFoundException(String name) {
        super("No sector found with name '" + name + "'");
    }
}
