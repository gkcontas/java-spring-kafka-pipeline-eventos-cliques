package com.gkcontas.clicks.exception;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        Map<String, String> fieldErrors
) {

    public static ErrorResponse ofValidation(int status, String error, Map<String, String> fieldErrors) {
        return new ErrorResponse(Instant.now(), status, error, "Validation error on the submitted fields", fieldErrors);
    }
}
