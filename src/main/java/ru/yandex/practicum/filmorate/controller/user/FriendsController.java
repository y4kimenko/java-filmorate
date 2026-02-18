package ru.yandex.practicum.filmorate.controller.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.user.response.UserResponseDto;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.service.friends.FriendsService;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.Collection;
import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{id}/friends")
public class FriendsController {
    private final FriendsService friendsService;

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
        friendsService.addFriend(id, friendId);
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
        friendsService.removeFriend(id, friendId);
    }

    @GetMapping()
    public List<UserResponseDto> getFriends(
            @PathVariable
            @NotNull(message = "id пользователя обязателен")
            @PositiveOrZero(message = "id пользователя не может быть отрицательным")
            Long id
    ) {
        return friendsService.getFriends(id);
    }

    @GetMapping("/common/{friendId}")
    public List<UserResponseDto> getMutualFriends(
            @PathVariable
            @NotNull(message = "id пользователя обязателен")
            @PositiveOrZero(message = "id пользователя не может быть отрицательным")
            Long id,

            @PathVariable
            @NotNull(message = "id друга обязателен")
            @PositiveOrZero(message = "id друга не может быть отрицательным")
            Long friendId
    ) {
        return friendsService.getMutualFriends(id, friendId);
    }
}
