package com.example.hangeulhunters.infrastructure.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    // 404 - Resource not found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    // 401 - Auth errors
    @ExceptionHandler({UnauthorizedException.class})
    public ResponseEntity<ErrorResponse> handleUnauthorized(Exception ex, HttpServletRequest req) {
        return build(HttpStatus.UNAUTHORIZED, "Unauthorized", req);
    }

    // 403 - Access denied
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        return build(HttpStatus.FORBIDDEN, "Access is denied", req);
    }

    // 400 - Bad request (validation + illegal arguments)
    @ExceptionHandler({BadRequestException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<?> handleBadRequest(Exception ex, HttpServletRequest req) {
        if (ex instanceof MethodArgumentNotValidException manv) {
            Map<String, String> errors = new HashMap<>();
            manv.getBindingResult().getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
            return buildValidation(HttpStatus.BAD_REQUEST, "Bad Request", errors, req);
        }
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    // 409 - Conflict (data integrity)
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(DataIntegrityViolationException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, "Conflict", req);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, HttpServletRequest req) {
        ErrorResponse error = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                req != null ? req.getRequestURI() : null,
                OffsetDateTime.now()
        );
        return new ResponseEntity<>(error, status);
    }

    private ResponseEntity<ValidationErrorResponse> buildValidation(HttpStatus status, String message, Map<String, String> errors, HttpServletRequest req) {
        ValidationErrorResponse error = new ValidationErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                req != null ? req.getRequestURI() : null,
                OffsetDateTime.now(),
                errors
        );
        return new ResponseEntity<>(error, status);
    }

    @Getter
    public static class ErrorResponse {
        private final int status;
        private final String error;
        private final String message;
        private final String path;
        private final OffsetDateTime timestamp;

        public ErrorResponse(int status, String error, String message, String path, OffsetDateTime timestamp) {
            this.status = status;
            this.error = error;
            this.message = message;
            this.path = path;
            this.timestamp = timestamp;
        }
    }

    @Getter
    public static class ValidationErrorResponse extends ErrorResponse {
        private final Map<String, String> errors;

        public ValidationErrorResponse(int status, String error, String message, String path, OffsetDateTime timestamp, Map<String, String> errors) {
            super(status, error, message, path, timestamp);
            this.errors = errors;
        }
    }
}