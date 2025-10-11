package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;


import java.util.*;

@RestController
@RequestMapping("/users")
@Log4j2
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @PostMapping
    public User createUser(@Valid @NotNull @RequestBody User user) {

        User newUser = new User();

        newUser.setId(getNextId());

        newUser.setEmail(user.getEmail());
        newUser.setLogin(user.getLogin());
        newUser.setName(user.getName());
        newUser.setBirthday(user.getBirthday());

        users.put(newUser.getId(), newUser);
        return newUser;
    }

    @PatchMapping
    public User updateUser(@Valid @NotNull @RequestBody User user) {

        if (users.containsKey(user.getId())) {
            User oldUser = users.get(user.getId());

            oldUser.setEmail(user.getEmail());
            oldUser.setLogin(user.getLogin());
            oldUser.setName(user.getName());
            oldUser.setBirthday(user.getBirthday());

            return oldUser;
        }
        throw new NotFoundException("User with id=" + user.getId() + " not found");
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }


    // Генерация нового Id
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
