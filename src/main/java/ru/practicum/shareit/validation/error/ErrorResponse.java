package ru.practicum.shareit.validation.error;

import lombok.Getter;
import lombok.Setter;

@Getter
public class ErrorResponse {

    private final String error;

    @Setter
    private String stackTrace;

    public ErrorResponse(String error) {
        this.error = error;
    }

}

