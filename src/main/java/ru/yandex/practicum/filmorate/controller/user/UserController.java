package ru.yandex.practicum.filmorate.controller.user;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
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
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    @PostMapping
    public User createUser(@Validated({Default.class}) @NotNull @RequestBody User user) {
        return service.createUser(user);
    }

    @PutMapping
    public User updateUser(@Validated({Default.class}) @NotNull @RequestBody User user) {
        return service.updateUser(user);
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return service.getAllUsers();
    }


}
