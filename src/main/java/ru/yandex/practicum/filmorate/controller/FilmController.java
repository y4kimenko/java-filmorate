package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final HashMap<Long, Film> films = new HashMap<>();

    @PostMapping
    public Film createFilm(@Validated({Default.class}) @NotNull @RequestBody Film film) {
        long t0 = System.nanoTime();

        log.debug("createFilm() – request name={}, releaseDate={}", film.getName(), film.getReleaseDate());
        film.setId(getNextId());

        films.put(film.getId(), film);

        long ms = (System.nanoTime() - t0) / 1_000_000;
        log.info("createFilm() – created id={} in {} ms", film.getId(), ms);

        return film;
    }

    @PutMapping
    public Film updateFilm(@Validated({Default.class}) @NotNull @RequestBody Film film) {
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

    @GetMapping
    public List<Film> getAllFilms() {
        int size = films.size();
        log.debug("getAllUsers() – total={}", size);

        return new ArrayList<>(films.values());
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
