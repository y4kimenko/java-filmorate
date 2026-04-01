package ru.yandex.practicum.filmorate.dto.film.request;

import ru.yandex.practicum.filmorate.dto.director.request.DirectorRequestDto;
import ru.yandex.practicum.filmorate.dto.genre.request.GenreRequestDto;
import ru.yandex.practicum.filmorate.dto.mpa.request.MpaRequestDto;

import java.time.LocalDate;
import java.util.Set;

public interface FilmRequestData {
    String name();

    String description();

    LocalDate releaseDate();

    Integer duration();

    MpaRequestDto mpa();

    Set<GenreRequestDto> genres();

    Set<DirectorRequestDto> directors();
}
