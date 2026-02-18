package ru.yandex.practicum.filmorate.dto.film.response;

import ru.yandex.practicum.filmorate.dto.genre.response.GenreResponseDto;
import ru.yandex.practicum.filmorate.dto.mpa.response.MpaResponseDto;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

public record FilmResponseDto(
    long id,
    String name,
    Set<GenreResponseDto> genres,
    MpaResponseDto mpa,
    String description,
    LocalDate releaseDate,
    Integer duration
) {
}
