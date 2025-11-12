package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@Validated
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    public Film createFilm(@Validated({Default.class}) @NotNull @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Validated({Default.class}) @NotNull @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @PutMapping("/{id}/like/{userId}")
    public void addUserLike(@PathVariable
                            @NotNull(message = "id фильма обязателен")
                            @PositiveOrZero(message = "id фильма не может быть отрицательным")
                            Long id,

                            @PathVariable
                            @NotNull(message = "id пользователя обязателен")
                            @PositiveOrZero(message = "id пользователя не может быть отрицательным")
                            Long userId
    ) {
        filmService.addUserLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeUserLike(@PathVariable
                               @NotNull(message = "id фильма обязателен")
                               @PositiveOrZero(message = "id фильма не может быть отрицательным")
                               Long id,

                               @PathVariable
                               @NotNull(message = "id пользователя обязателен")
                               @PositiveOrZero(message = "id пользователя не может быть отрицательным")
                               Long userId
    ) {
        filmService.removeUserLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10") @PositiveOrZero(message = "count  не может быть отрицательным") int count) {
        return filmService.getPopularFilms(count);
    }
}
