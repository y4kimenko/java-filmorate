package ru.yandex.practicum.filmorate.controller.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@Validated
@RequiredArgsConstructor
@Log4j2
@RestController
@RequestMapping("/users/{id}/friends")
public class FriendsController {
    private final UserService service;

    @PutMapping("/{friendId}")
    public void addFriend(
            @PathVariable
            @NotNull(message = "id пользователя обязателен")
            @PositiveOrZero(message = "id пользователя не может быть отрицательным")
            Long id,

            @PathVariable
            @NotNull(message = "id друга обязателен")
            @PositiveOrZero(message = "id друга не может быть отрицательным")
            Long friendId
    ) {
        service.addFriend(id, friendId);
    }

    @DeleteMapping("/{friendId}")
    public void removeFriend(
            @PathVariable
            @NotNull(message = "id пользователя обязателен")
            @PositiveOrZero(message = "id пользователя не может быть отрицательным")
            Long id,

            @PathVariable
            @NotNull(message = "id друга обязателен")
            @PositiveOrZero(message = "id друга не может быть отрицательным")
            Long friendId
    ) {
        service.removeFriend(id, friendId);
    }

    @GetMapping()
    public Collection<User> getFriends(
            @PathVariable
            @NotNull(message = "id пользователя обязателен")
            @PositiveOrZero(message = "id пользователя не может быть отрицательным")
            Long id
    ) {
        return service.getFriends(id);
    }

    @GetMapping("/common/{friendId}")
    public Collection<User> getMutualFriends(
            @PathVariable
            @NotNull(message = "id пользователя обязателен")
            @PositiveOrZero(message = "id пользователя не может быть отрицательным")
            Long id,

            @PathVariable
            @NotNull(message = "id друга обязателен")
            @PositiveOrZero(message = "id друга не может быть отрицательным")
            Long friendId
    ) {
        return service.getMutualFriends(id, friendId);
    }
}
