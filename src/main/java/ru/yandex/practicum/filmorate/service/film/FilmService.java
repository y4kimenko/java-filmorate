package ru.yandex.practicum.filmorate.service.film;


import ru.yandex.practicum.filmorate.dto.film.request.FilmRequestCreateDto;
import ru.yandex.practicum.filmorate.dto.film.request.FilmRequestUpdateDto;
import ru.yandex.practicum.filmorate.dto.film.response.FilmResponseDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;


public interface FilmService {

    FilmResponseDto createFilm(FilmRequestCreateDto filmRequestDto);

    FilmResponseDto updateFilm(FilmRequestUpdateDto dto);

    List<FilmResponseDto> getAllFilms();

    List<FilmResponseDto> getPopularFilms(int count);

    List<FilmResponseDto> getCommonFilms(long userId, long friendId);

    FilmResponseDto getById(Long filmId);

    List<FilmResponseDto> getMostPopularFilms(long count, long genreId, long year);
}
