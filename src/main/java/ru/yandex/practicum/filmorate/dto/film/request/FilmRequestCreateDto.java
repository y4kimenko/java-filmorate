package ru.yandex.practicum.filmorate.dto.film.request;

import jakarta.validation.constraints.*;
import ru.yandex.practicum.filmorate.dto.genre.request.GenreRequestDto;
import ru.yandex.practicum.filmorate.dto.mpa.request.MpaRequestDto;

import java.time.LocalDate;
import java.util.Set;

public record FilmRequestCreateDto(
        @NotBlank(message = "name не должно состоять из пробелов")
        String name,

        Set<GenreRequestDto> genres,

        MpaRequestDto mpa,

        @Size(max = 200, message = "у description максимальная длина 200 символов")
        String description,

        @NotNull(message = "releaseDate не может быть пустой")
        LocalDate releaseDate,

        @NotNull(message = "duration не может быть пустым")
        @Min(value = 1, message = "duration должна составлять не меньше 1 минуты")
        Integer duration
) implements FilmRequestData {
    private static final LocalDate MIN = LocalDate.of(1895, 12, 28);

    @AssertTrue(message = "releaseDate не может быть раньше чем 28.12.1895")
    public boolean isReleaseDateValid() {
        return releaseDate == null || !releaseDate.isBefore(MIN);
    }
}
