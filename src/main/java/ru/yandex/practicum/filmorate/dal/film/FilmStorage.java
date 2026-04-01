package ru.yandex.practicum.filmorate.dal.film;

import ru.yandex.practicum.filmorate.enums.FilmsPopularSortBy;
import ru.yandex.practicum.filmorate.enums.FilmsSearchBy;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


public interface FilmStorage {

    Film save(Film film);

    Film update(Film film);

    List<Film> getAll();

    List<Film> getCommonFilms(long userId, long friendId);

    Optional<Film> findById(long id);

    Set<Film> findByIds(Set<Long> ids);

    boolean existsById(long id);

    List<Film> getRecommendations(long userId);

    List<Film> getMostPopularFilms(Long count, Map<FilmsPopularSortBy, Long> filters);

    int deleteById(long id);

    Map<Long, Film> searchByTitle(String title, List<FilmsSearchBy> searchBy);
}
