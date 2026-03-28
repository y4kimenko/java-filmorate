package ru.yandex.practicum.filmorate.dto.film.response;

import ru.yandex.practicum.filmorate.dto.director.response.DirectorResponseDto;
import ru.yandex.practicum.filmorate.dto.genre.response.GenreResponseDto;
import ru.yandex.practicum.filmorate.dto.mpa.response.MpaResponseDto;

import java.time.LocalDate;
import java.util.List;

public record FilmResponseDto(
        long id,
        String name,
        List<GenreResponseDto> genres,
        MpaResponseDto mpa,
        List<DirectorResponseDto> directors,
        String description,
        LocalDate releaseDate,
        Integer duration
) {
}
