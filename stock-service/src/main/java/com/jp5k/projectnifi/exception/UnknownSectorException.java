package com.jp5k.projectnifi.exception;

/**
 * Thrown when a {@code Stock} is created or replaced with a {@code sector}
 * that {@code sector-service} doesn't recognize.
 *
 * <p>Distinct from {@link StockNotFoundException}: that one means "no stock
 * has this symbol", this one means "the sector this stock claims to belong to
 * doesn't exist" — a {@code 400 Bad Request} (bad input), not a
 * {@code 404 Not Found}, since it's the request body that's invalid, not a
 * missing resource identified by the URL.
 *
 * <p>Extends {@link RuntimeException} (unchecked) deliberately, for the same
 * reason as {@link StockNotFoundException} — a client error to be reported,
 * not something most call sites can meaningfully recover from.
 */
public class UnknownSectorException extends RuntimeException {

    /**
     * @param sector the sector name that {@code sector-service} doesn't
     *     recognize; embedded in the message so logs and the 400 response
     *     body say which sector was meant
     */
    public UnknownSectorException(String sector) {
        super("No sector found with name '" + sector + "'");
    }
}
