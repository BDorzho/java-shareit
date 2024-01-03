package ru.practicum.shareit.validation.error;


import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ErrorResponse {

    private String error;

    private String stackTrace;

    public ErrorResponse(String error) {
        this.error = error;
    }
}

