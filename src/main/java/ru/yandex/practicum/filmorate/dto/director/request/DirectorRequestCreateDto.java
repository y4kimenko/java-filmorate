package ru.yandex.practicum.filmorate.dto.director.request;

import jakarta.validation.constraints.NotBlank;

public record DirectorRequestCreateDto(
        @NotBlank(message = "name не должно состоять из пробелов")
        String name
) {
}
