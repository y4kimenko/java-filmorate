package ru.yandex.practicum.filmorate.dto.review.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ReviewRequestCreateDto(
        @NotNull
        @PositiveOrZero
        Long userId,

        @NotNull
        @PositiveOrZero
        Long filmId,

        @NotNull
        @NotEmpty
        String content,

        @NotNull
        Boolean isPositive
) implements ReviewRequestData {
}

