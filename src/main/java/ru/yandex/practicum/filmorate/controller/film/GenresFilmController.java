package ru.yandex.practicum.filmorate.controller.film;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.genre.response.GenreResponseDto;
import ru.yandex.practicum.filmorate.service.genres.GenresService;

import java.util.Set;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
@Validated
public class GenresFilmController {
    private final GenresService genresService;

    @GetMapping
    public Set<GenreResponseDto> getGenres() {
        return genresService.getAll();
    }

    @GetMapping("/{id}")
    public GenreResponseDto getGenreById(@PathVariable
                                         @NotNull(message = "id genre обязателен")
                                         @PositiveOrZero(message = "id genre не может быть отрицательным")
                                         long id
    ) {
        return genresService.getById(id);
    }
}
