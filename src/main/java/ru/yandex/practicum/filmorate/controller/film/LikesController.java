package ru.yandex.practicum.filmorate.controller.film;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.likes.LikesService;

@RestController
@RequestMapping("/films/{filmId}/like/{userId}")
@RequiredArgsConstructor
@Validated
public class LikesController {
    private final LikesService likeService;

    @PutMapping
    public void addUserLike(@PathVariable
                            @NotNull(message = "id фильма обязателен")
                            @PositiveOrZero(message = "id фильма не может быть отрицательным")
                            Long filmId,

                            @PathVariable
                            @NotNull(message = "id пользователя обязателен")
                            @PositiveOrZero(message = "id пользователя не может быть отрицательным")
                            Long userId
    ) {
        likeService.addUserLike(filmId, userId);
    }

    @DeleteMapping
    public void removeUserLike(@PathVariable
                               @NotNull(message = "id фильма обязателен")
                               @PositiveOrZero(message = "id фильма не может быть отрицательным")
                               Long filmId,

                               @PathVariable
                               @NotNull(message = "id пользователя обязателен")
                               @PositiveOrZero(message = "id пользователя не может быть отрицательным")
                               Long userId
    ) {
        likeService.removeUserLike(filmId, userId);
    }
}
