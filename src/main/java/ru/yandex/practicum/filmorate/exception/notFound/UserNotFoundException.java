package ru.yandex.practicum.filmorate.exception.notFound;

public class  UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
