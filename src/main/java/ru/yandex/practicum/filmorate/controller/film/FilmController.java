package ru.yandex.practicum.filmorate.controller.film;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.film.request.FilmRequestCreateDto;
import ru.yandex.practicum.filmorate.dto.film.request.FilmRequestUpdateDto;
import ru.yandex.practicum.filmorate.dto.film.response.FilmResponseDto;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.List;


@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@Validated
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmResponseDto createFilm(@Validated({Default.class})
                                      @NotNull
                                      @RequestBody
                                      FilmRequestCreateDto film
    ) {
        return filmService.createFilm(film);
    }

    @PutMapping
    public FilmResponseDto updateFilm(@Validated({Default.class})
                                      @NotNull
                                      @RequestBody
                                      FilmRequestUpdateDto film
    ) {
        return filmService.updateFilm(film);
    }

    @GetMapping
    public List<FilmResponseDto> getAllFilms() {
        return filmService.getAllFilms();
    }


    @GetMapping("/popular")
    public List<FilmResponseDto> getPopularFilms(@RequestParam(defaultValue = "10")
                                                 @PositiveOrZero(message = "count  не может быть отрицательным")
                                                 int count
    ) {
        return filmService.getPopularFilms(count);
    }

    @GetMapping("/common")
    public List<FilmResponseDto> getCommonFilms(
            @RequestParam
            @NotNull(message = "userId обязателен")
            @PositiveOrZero(message = "userId не может быть отрицательным")
            Long userId,

            @RequestParam
            @NotNull(message = "friendId обязателен")
            @PositiveOrZero(message = "friendId не может быть отрицательным")
            Long friendId
    ) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/{id}")
    public FilmResponseDto getFilmById(@PathVariable
                                       @PositiveOrZero(message = "id не может быть отрицательным")
                                       Long id
    ) {
        return filmService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable @PositiveOrZero(message = "id не может быть отрицательным") Long id) {
        filmService.deleteById(id);
    }
}
