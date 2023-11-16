package ru.practicum.shareit.validation.error;

import lombok.Getter;

@Getter
public class ErrorResponse {

    private final String error;

    private String stackTrace;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }
}

