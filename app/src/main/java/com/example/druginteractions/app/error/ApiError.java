package com.example.druginteractions.app.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
    String code,
    String message,
    Map<String, Object> details,
    Instant timestamp
) {
    public ApiError(String code, String message, Map<String, Object> details) {
        this(code, message, details, Instant.now());
    }

    public ApiError(String code, String message) {
        this(code, message, null, Instant.now());
    }
}
