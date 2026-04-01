package ru.yandex.practicum.filmorate.exception;

public class DuplicateReviewReactionException extends RuntimeException {
    public DuplicateReviewReactionException(String message) {
        super(message);
    }
}
