package ru.yandex.practicum.filmorate.exception.notFound;

public class ReviewNotFoundException extends NotFoundException {
    public ReviewNotFoundException(String message) {
        super(message);
    }
}
