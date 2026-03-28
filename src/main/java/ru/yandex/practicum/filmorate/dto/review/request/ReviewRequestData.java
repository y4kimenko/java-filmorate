package ru.yandex.practicum.filmorate.dto.review.request;

public interface ReviewRequestData {
    Long userId();

    Long filmId();

    String content();

    Boolean isPositive();
}
