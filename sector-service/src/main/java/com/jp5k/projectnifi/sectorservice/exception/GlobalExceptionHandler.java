package com.jp5k.projectnifi.sectorservice.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Single place where every exception escaping a controller is turned into an
 * HTTP error response.
 *
 * <p>Mirrors {@code stock-service}'s {@code GlobalExceptionHandler} — same
 * rationale: consistency (every error comes back as {@link ProblemDetail}),
 * no leakage (stack traces/internal detail never reach the client), and thin
 * controllers (methods can throw {@link SectorNotFoundException} and
 * otherwise assume the happy path).
 *
 * <p>Extends {@link ResponseEntityExceptionHandler} so Spring MVC's own
 * exceptions (unreadable JSON, missing parameters, wrong content type, ...)
 * already get consistent {@code ProblemDetail} handling; we add the
 * application-specific cases on top.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Maps a missing sector to {@code 404 Not Found}. The exception message
     * names the sector and is safe to echo back (it contains only the value
     * the client supplied), so it goes in {@code detail}.
     */
    @ExceptionHandler(SectorNotFoundException.class)
    public ProblemDetail handleSectorNotFound(SectorNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Sector not found");
        return problem;
    }

    /**
     * Last line of defence: anything not handled more specifically becomes a
     * generic {@code 500}. The real exception is logged at ERROR (with its
     * stack trace, server-side only); the client just gets a fixed, detail-free
     * message so nothing internal escapes.
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex) {
        log.error("Unhandled exception serving request", ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred. Please try again later.");
        problem.setTitle("Internal server error");
        return problem;
    }

    /**
     * Turns {@code @Valid} failures into {@code 400 Bad Request} with a
     * per-field breakdown under an {@code errors} property, so a client can see
     * exactly which fields were rejected and why. Only the field name and the
     * validation message are exposed — both are safe, neither reflects the
     * submitted values back.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        // LinkedHashMap: keep the fields in declaration order for a stable,
        // readable response rather than whatever order the validator reports.
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            // putIfAbsent: if one field trips two constraints, report the first
            // message only — keeps the payload predictable.
            fieldErrors.putIfAbsent(
                    error.getField(),
                    error.getDefaultMessage() == null ? "invalid value" : error.getDefaultMessage());
        }

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "One or more fields are invalid");
        problem.setTitle("Validation failed");
        problem.setProperty("errors", fieldErrors);
        return ResponseEntity.badRequest().body(problem);
    }
}
