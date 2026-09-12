/**
 * Custom exceptions and centralized exception handling for {@code sector-service}.
 *
 * <p>Classes here define domain-specific exceptions (e.g. a "sector not
 * found" case) plus a {@code @ControllerAdvice} that translates them into
 * consistent HTTP error responses, without leaking stack traces or other
 * internal detail to the client.
 */
package com.jp5k.projectnifi.sectorservice.exception;
