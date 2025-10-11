package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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
    public Film createFilm(@Validated({/*Film.OnCreate.class,*/ Default.class}) @NotNull @RequestBody Film film) {
        long t0 = System.nanoTime();

        log.debug("createFilm() – request name={}, releaseDate={}", film.getName(), film.getReleaseDate());

        Film newFilm = new Film();

        newFilm.setId(getNextId());

        newFilm.setName(film.getName());
        newFilm.setDescription(film.getDescription());
        newFilm.setReleaseDate(film.getReleaseDate());
        newFilm.setDuration(film.getDuration());

        films.put(newFilm.getId(), newFilm);

        long ms = (System.nanoTime() - t0) / 1_000_000;
        log.info("createFilm() – created id={} in {} ms", newFilm.getId(), ms);

        return newFilm;
    }

    @PatchMapping
    public Film updateFilm(@Validated({/*Film.OnUpdate.class,*/ Default.class}) @NotNull @RequestBody Film film) {
        long t0 = System.nanoTime();
        log.debug("updateFilm() – request id={}, name={}, releaseDate={}",
                film.getId(), film.getName(), film.getReleaseDate());

        if (films.containsKey(film.getId())) {
            Film oldFilm = films.get(film.getId());

            oldFilm.setName(film.getName());
            oldFilm.setDescription(film.getDescription());
            oldFilm.setReleaseDate(film.getReleaseDate());
            oldFilm.setDuration(film.getDuration());

            long ms = (System.nanoTime() - t0) / 1_000_000;
            log.info("updateUser() – updated id={} in {} ms, updateUser={}", film.getId(), ms, oldFilm);

            return oldFilm;
        }
        throw new IllegalArgumentException("Film not found");
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
