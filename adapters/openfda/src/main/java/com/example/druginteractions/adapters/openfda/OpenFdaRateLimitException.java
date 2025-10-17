package com.example.druginteractions.adapters.openfda;

public class OpenFdaRateLimitException extends OpenFdaException {
    public OpenFdaRateLimitException(String message, Throwable cause) {
        super(message, cause);
    }
}
