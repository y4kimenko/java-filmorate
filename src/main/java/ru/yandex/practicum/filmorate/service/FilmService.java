package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        requireFilm(film.getId());

        return filmStorage.updateFilm(film);

    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void addUserLike(long filmId, long userId) {
        requireUser(userId);

        requireFilm(filmId).getLikedUser().add(userId);
    }

    public void removeUserLike(long filmId, long userId) {
        requireUser(userId);

        requireFilm(filmId).getLikedUser().remove(userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt(
                        (Film film) -> film.getLikedUser().size()
                ).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    private Film requireFilm(long filmId) {
        return filmStorage.getFilmById(filmId)
                .orElseThrow(() ->
                        new FilmNotFoundException("Фильм с id " + filmId + " не найден")
                );
    }

    private void requireUser(long userId) {
        userStorage.getUserById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("Пользователь с id " + userId + " не найден")
                );
    }

}
