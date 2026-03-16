package ru.yandex.practicum.filmorate.controller.user;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.film.response.FilmResponseDto;
import ru.yandex.practicum.filmorate.dto.user.request.UserRequestCreateDto;
import ru.yandex.practicum.filmorate.dto.user.request.UserRequestUpdateDto;
import ru.yandex.practicum.filmorate.dto.user.response.UserResponseDto;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.List;


@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService service;
    private final FilmService filmService;

    @PostMapping
    public UserResponseDto createUser(@Validated({Default.class}) @NotNull @RequestBody UserRequestCreateDto user) {
        return service.create(user);
    }

    @PutMapping
    public UserResponseDto updateUser(@Validated({Default.class}) @NotNull @RequestBody UserRequestUpdateDto user) {
        return service.update(user);
    }

    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return service.getAll();
    }

    @GetMapping("/{id}/recommendations")
    public List<FilmResponseDto> getRecommendations(
            @PathVariable
            @NotNull(message = "id пользователя обязателен")
            @PositiveOrZero(message = "id пользователя не может быть отрицательным")
            Long id
    ) {
        return filmService.getRecommendations(id);
    }

}
