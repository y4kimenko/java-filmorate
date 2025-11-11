package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

@Component
@Log4j2
public class InMemoryFilmStorage implements FilmStorage {
    private final HashMap<Long, Film> films = new HashMap<>();

    @Override
    public Film createFilm(Film film) {
        long t0 = System.nanoTime();

        log.debug("createFilm() – request name={}, releaseDate={}", film.getName(), film.getReleaseDate());
        film.setId(getNextId());

        films.put(film.getId(), film);

        long ms = (System.nanoTime() - t0) / 1_000_000;
        log.info("createFilm() – created id={} in {} ms", film.getId(), ms);

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        long t0 = System.nanoTime();
        log.debug("updateFilm() – request id={}, name={}, releaseDate={}",
                film.getId(), film.getName(), film.getReleaseDate());


        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);

            long ms = (System.nanoTime() - t0) / 1_000_000;
            log.info("updateUser() – updated id={} in {} ms, updateUser={}", film.getId(), ms, film);

            return film;
        }
        throw new NotFoundException("Film not found");
    }

    @Override
    public Collection<Film> getAllFilms() {
        int size = films.size();
        log.debug("getAllUsers() – total={}", size);

        return new ArrayList<>(films.values());
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        return Optional.ofNullable(films.get(id));
    }


    // Генерация нового Id
    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
