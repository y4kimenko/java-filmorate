package ru.yandex.practicum.filmorate.service.film;


import ru.yandex.practicum.filmorate.dto.film.request.FilmRequestCreateDto;
import ru.yandex.practicum.filmorate.dto.film.request.FilmRequestUpdateDto;
import ru.yandex.practicum.filmorate.dto.film.response.FilmResponseDto;
import ru.yandex.practicum.filmorate.enums.DirectorFilmsSortBy;
import ru.yandex.practicum.filmorate.enums.FilmsPopularSortBy;
import ru.yandex.practicum.filmorate.enums.FilmsSearchBy;

import java.util.List;
import java.util.Map;


public interface FilmService {

    FilmResponseDto createFilm(FilmRequestCreateDto filmRequestDto);

    FilmResponseDto updateFilm(FilmRequestUpdateDto dto);

    List<FilmResponseDto> getAllFilms();

    FilmResponseDto getById(Long filmId);

    List<FilmResponseDto> getMostPopularFilms(long count, Map<FilmsPopularSortBy, Long> filters);

    void deleteById(long id);

    List<FilmResponseDto> getCommonFilms(long userId, long friendId);

    List<FilmResponseDto> getDirectorFilms(long directorId, DirectorFilmsSortBy sortBy);

    List<FilmResponseDto> searchFilms(String query, List<FilmsSearchBy> searchBy);

    List<FilmResponseDto> getRecommendations(long userId);
}