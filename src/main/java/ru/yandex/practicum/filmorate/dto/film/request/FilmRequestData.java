package ru.yandex.practicum.filmorate.dto.film.request;

import ru.yandex.practicum.filmorate.dto.genre.request.GenreRequestDto;
import ru.yandex.practicum.filmorate.dto.mpa.request.MpaRequestDto;

public interface FilmRequestData {
    String name();

    String description();

    java.time.LocalDate releaseDate();

    Integer duration();

    MpaRequestDto mpa();

    java.util.Set<GenreRequestDto> genres();
}
