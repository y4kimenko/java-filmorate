package ru.yandex.practicum.filmorate.dal.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;


public interface FilmStorage {

    Film save(Film film);

    Film update(Film film);

    List<Film> getAll();

    List<Film> getPopularFilms(long limit);

    List<Film> getCommonFilms(long userId, long friendId);

    Optional<Film> getById(long id);

    boolean existsById(long id);

    List<Film> getMostPopularFilms(long count, long genreId, long year);
    int deleteById(long id);
}
