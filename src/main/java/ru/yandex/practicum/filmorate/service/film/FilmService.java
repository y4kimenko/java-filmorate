package ru.yandex.practicum.filmorate.service.film;


import ru.yandex.practicum.filmorate.dto.film.request.FilmRequestCreateDto;
import ru.yandex.practicum.filmorate.dto.film.request.FilmRequestUpdateDto;
import ru.yandex.practicum.filmorate.dto.film.response.FilmResponseDto;

import java.util.List;


public interface FilmService {

    FilmResponseDto createFilm(FilmRequestCreateDto filmRequestDto);

    FilmResponseDto updateFilm(FilmRequestUpdateDto dto);

    List<FilmResponseDto> getAllFilms();

    List<FilmResponseDto> getPopularFilms(int count);

    List<FilmResponseDto> getCommonFilms(long userId, long friendId);

    FilmResponseDto getById(Long filmId);

    void deleteById(long id);
}
