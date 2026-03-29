package ru.yandex.practicum.filmorate.web.controller.user;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.service.event.EventService;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.List;


@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final FilmService filmService;
    private final EventService eventService;

    @PostMapping
    public UserResponseDto createUser(@Valid
                                      @NotNull
                                      @RequestBody
                                      UserRequestCreateDto user
    ) {
        return userService.create(user);
    }

    @PutMapping
    public UserResponseDto updateUser(@Valid
                                      @NotNull
                                      @RequestBody
                                      UserRequestUpdateDto user
    ) {
        return userService.update(user);
    }

    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return userService.getAll();
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

    @GetMapping("/{id}/feed")
    public List<Event> getFeedFromUser(
            @PathVariable
            @PositiveOrZero(message = "id не может быть отрицательным")
            Long id) {
        return eventService.getFeed(id);
    }

    @GetMapping("/{id}")
    public UserResponseDto getUserById(@PathVariable
                                       @PositiveOrZero(message = "id не может быть отрицательным")
                                       long id
    ) {
        return userService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable
                           @PositiveOrZero(message = "id не может быть отрицательным")
                           Long id
    ) {
        userService.deleteById(id);
    }
}
