package ru.yandex.practicum.filmorate.web.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.yandex.practicum.filmorate.exception.DuplicateReviewException;
import ru.yandex.practicum.filmorate.exception.DuplicateReviewReactionException;
import ru.yandex.practicum.filmorate.exception.notFound.NotFoundException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ErrorHandler {

    // 1. Ошибки валидации тела запроса (@Valid @RequestBody)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {

        Map<String, String> fieldErrors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String field = error.getField();
            String message = error.getDefaultMessage();
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
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {

        Map<String, String> errors = new HashMap<>();

        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String path = violation.getPropertyPath().toString();
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

    // 3. Ошибки валидации параметров строки запроса
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMissingRequestParam(
            MissingServletRequestParameterException ex
    ) {
        return Map.of(
                "error", "Параметр '" + ex.getParameterName() + "' обязателен"
        );
    }

    // 4. Общий ValidationException
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
        ErrorResponse response = new ErrorResponse(
                "Ошибка валидации",
                ex.getClass().getSimpleName()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 5. NotFoundException -> 404
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(NotFoundException ex) {
        ErrorResponse response = new ErrorResponse(
                ex.getMessage(),
                ex.getClass().getSimpleName()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // 6. IllegalStateException
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalException(IllegalStateException ex) {
        ErrorResponse response = new ErrorResponse(
                ex.getMessage(),
                ex.getClass().getSimpleName()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // 7. DuplicateReviewException
    @ExceptionHandler(DuplicateReviewException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateReview(DuplicateReviewException ex) {
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                ex.getClass().getSimpleName()
        );

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    // 8. DuplicateReviewReactionException
    @ExceptionHandler(DuplicateReviewReactionException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateReview(DuplicateReviewReactionException ex) {
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                ex.getClass().getSimpleName()
        );

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }
}

