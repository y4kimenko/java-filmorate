package ru.yandex.practicum.filmorate.controller.user;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.user.request.UserRequestCreateDto;
import ru.yandex.practicum.filmorate.dto.user.request.UserRequestUpdateDto;
import ru.yandex.practicum.filmorate.dto.user.response.UserResponseDto;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.List;


@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService service;

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

    @GetMapping("/{id}")
    public UserResponseDto getUserById(@PathVariable long id) {
        return service.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") @PositiveOrZero(message = "id не может быть отрицательным") Long id) {
        service.deleteById(id);
    }
}
