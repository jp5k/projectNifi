/**
 * Custom exceptions and centralized exception handling for {@code stock-service}.
 *
 * <p>Classes here define domain-specific exceptions (e.g. a "stock not
 * found" case) plus a {@code @ControllerAdvice} that translates them into
 * consistent HTTP error responses, without leaking stack traces or other
 * internal detail to the client.
 */
package com.jp5k.projectnifi.exception;
