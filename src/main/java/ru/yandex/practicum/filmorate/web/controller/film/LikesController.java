package ru.yandex.practicum.filmorate.web.controller.film;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
                            // @PositiveOrZero(message = "id фильма не может быть отрицательным") удаление обусловлено тестами
                            Long filmId,

                            @PathVariable
                            @NotNull(message = "id пользователя обязателен")
                            // @PositiveOrZero(message = "id пользователя не может быть отрицательным") удаление обусловлено тестами
                            Long userId
    ) {
        likeService.addUserLike(filmId, userId);
    }

    @DeleteMapping
    public void removeUserLike(@PathVariable
                               @NotNull(message = "id фильма обязателен")
                               // @PositiveOrZero(message = "id фильма не может быть отрицательным") удаление обусловлено тестами
                               Long filmId,

                               @PathVariable
                               @NotNull(message = "id пользователя обязателен")
                               // @PositiveOrZero(message = "id пользователя не может быть отрицательным") удаление обусловлено тестами
                               Long userId
    ) {
        likeService.removeUserLike(filmId, userId);
    }
}
