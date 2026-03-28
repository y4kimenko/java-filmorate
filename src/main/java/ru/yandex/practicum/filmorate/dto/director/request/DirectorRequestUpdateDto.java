package ru.yandex.practicum.filmorate.dto.director.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record DirectorRequestUpdateDto(
        @NotNull(message = "id не может пустым")
        @PositiveOrZero(message = "id должно быть больше 0")
        Long id,
        @NotBlank(message = "name не должно состоять из пробелов")
        String name
) {
}
