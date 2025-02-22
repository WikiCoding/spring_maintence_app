package com.wikicoding.maintenance.controllers.exceptions;

import java.time.LocalDateTime;

public class ErrorDto {
    private final String message;
    private final LocalDateTime timestamp;

    public ErrorDto(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
