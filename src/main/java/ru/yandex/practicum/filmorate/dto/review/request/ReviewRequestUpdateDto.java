package ru.yandex.practicum.filmorate.dto.review.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ReviewRequestUpdateDto(
        @NotNull
        Long reviewId,

        @NotNull
        Long userId,

        @NotNull
        Long filmId,

        @NotNull
        @NotEmpty
        String content,

        @NotNull
        Boolean isPositive
) implements ReviewRequestData {
}
