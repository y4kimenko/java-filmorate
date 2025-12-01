package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ErrorHandler {

    // 1. Ошибки валидации тела запроса (@Valid @RequestBody)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {

        Map<String, String> fieldErrors = new HashMap<>();

        // Проходимся по всем FieldError и собираем поле → сообщение
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String field = error.getField();                 // имя поля, например "email"
            String message = error.getDefaultMessage();      // текст сообщения
            fieldErrors.put(field, message);
        });

        ErrorResponse response = new ErrorResponse(
                "Ошибка валидации входных данных",
                ex.getClass().getSimpleName(),
                fieldErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 2. Ошибки валидации параметров методов (ConstraintViolationException)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {

        Map<String, String> errors = new HashMap<>();

        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String path = violation.getPropertyPath().toString();
            // Обычно path вида "createUser.userDto.email" или "addFriend.id"
            String field = extractLastPathPart(path);
            errors.put(field, violation.getMessage());
        }

        ErrorResponse response = new ErrorResponse(
                "Ошибка валидации параметров",
                ex.getClass().getSimpleName(),
                errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    private String extractLastPathPart(String path) {
        int dotIndex = path.lastIndexOf('.');
        if (dotIndex != -1 && dotIndex < path.length() - 1) {
            return path.substring(dotIndex + 1);
        }
        return path;
    }

    // 3. Общий случай ValidationException – вдруг что–то ещё полетит отсюда
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
        ErrorResponse response = new ErrorResponse(
                "Ошибка валидации",
                ex.getClass().getSimpleName()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 4. Обработка своих бизнес–исключений – NotFoundException на 404
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(NotFoundException ex) {
        ErrorResponse response = new ErrorResponse(
                ex.getMessage(),                    // "Пользователь/фильм не найден"
                ex.getClass().getSimpleName()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

}

