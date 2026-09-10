package com.jp5k.projectnifi.exception;

/**
 * Thrown when an operation targets a {@code Stock} symbol that doesn't exist.
 *
 * <p>The controller throws this instead of hand-assembling a 404
 * {@code ResponseEntity} at each call site: the mapping from "no such stock"
 * to an HTTP 404 (and its response body) lives in one place,
 * {@link GlobalExceptionHandler}. That keeps the controller methods reading as
 * straight-line happy path, and guarantees every not-found response looks the
 * same.
 *
 * <p>Extends {@link RuntimeException} (unchecked) deliberately — a missing
 * stock is a client error to be reported, not something most call sites can
 * meaningfully recover from, so forcing {@code throws}/{@code catch} plumbing
 * through the service and controller would add noise without value.
 */
public class StockNotFoundException extends RuntimeException {

    /**
     * @param symbol the ticker symbol that could not be found; embedded in the
     *     message so logs and the 404 response body say which stock was meant
     */
    public StockNotFoundException(String symbol) {
        super("No stock found with symbol '" + symbol + "'");
    }
}
