package ru.yandex.practicum.filmorate.dto.review.response;

public record ReviewResponseDto(
        long reviewId,
        long userId,
        long filmId,
        String content,
        boolean isPositive,
        long useful
) {
}
