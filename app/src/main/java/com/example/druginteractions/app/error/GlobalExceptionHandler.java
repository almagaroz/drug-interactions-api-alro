package com.example.druginteractions.app.error;

import com.example.druginteractions.adapters.openfda.OpenFdaException;
import com.example.druginteractions.adapters.openfda.OpenFdaRateLimitException;
import com.example.druginteractions.adapters.openfda.OpenFdaQueryException;
import com.example.druginteractions.domain.InteractionNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(ConstraintViolationException ex) {
        var violations = ex.getConstraintViolations().stream()
            .collect(Collectors.toMap(
                violation -> violation.getPropertyPath().toString(),
                violation -> violation.getMessage()
            ));

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ApiError(
                "VALIDATION_ERROR",
                "Validation failed",
                new HashMap<>(violations)
            ));
    }

    @ExceptionHandler(InteractionNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(InteractionNotFoundException ex) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ApiError(
                "NOT_FOUND",
                ex.getMessage()
            ));
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ApiError> handleUpstreamError(WebClientResponseException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_GATEWAY)
            .body(new ApiError(
                "UPSTREAM_ERROR",
                "Error communicating with upstream service",
                Map.of(
                    "status", ex.getStatusCode().value(),
                    "reason", ex.getStatusText()
                )
            ));
    }

    @ExceptionHandler(OpenFdaException.class)
    public ResponseEntity<ApiError> handleOpenFdaError(OpenFdaException ex) {
        var details = new HashMap<String, Object>();
        if (ex instanceof OpenFdaRateLimitException) {
            details.put("reason", "RATE_LIMIT_EXCEEDED");
        } else if (ex instanceof OpenFdaQueryException) {
            details.put("reason", "INVALID_QUERY");
        } else {
            details.put("reason", "UPSTREAM_ERROR");
        }

        return ResponseEntity
            .status(HttpStatus.BAD_GATEWAY)
            .body(new ApiError(
                "OPENFDA_ERROR",
                ex.getMessage(),
                details
            ));
    }
}
