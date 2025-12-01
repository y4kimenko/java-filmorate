package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class ErrorResponse {

    // Человекочитаемое сообщение для клиента
    private String message;

    // Техническое сообщение – тип исключения или подробности
    private String debugMessage;

    // Ошибки по полям (для валидации)
    private Map<String, String> errors;

    public ErrorResponse() {
    }

    public ErrorResponse(String message, String debugMessage, Map<String, String> errors) {
        this.message = message;
        this.debugMessage = debugMessage;
        this.errors = errors;
    }

    public ErrorResponse(String message, String debugMessage) {
        this(message, debugMessage, null);
    }


}
