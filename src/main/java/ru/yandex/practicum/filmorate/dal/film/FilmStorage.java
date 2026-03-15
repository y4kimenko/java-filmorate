package ru.yandex.practicum.filmorate.dal.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;


public interface FilmStorage {

    Film save(Film film);

    Film update(Film film);

    LinkedHashMap<Long, Film> getAll();

    List<Film> getPopularFilms(long limit);

    Optional<Film> getById(long id);

    boolean existsById(long id);

    void deleteById(long id);
}
