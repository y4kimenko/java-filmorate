package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Log4j2
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @PostMapping
    public User createUser(@Validated({/*User.OnCreate.class,*/ Default.class}) @NotNull @RequestBody User user) {
        long t0 = System.nanoTime();

        log.debug("createUser() – request login={}, email={}", user.getLogin(), user.getEmail());

        User newUser = new User();

        newUser.setId(getNextId());

        newUser.setEmail(user.getEmail());
        newUser.setLogin(user.getLogin());
        newUser.setName(user.getName());
        newUser.setBirthday(user.getBirthday());

        users.put(newUser.getId(), newUser);

        long ms = (System.nanoTime() - t0) / 1_000_000;
        log.info("createUser() – created id={} in {} ms", newUser.getId(), ms);

        return newUser;
    }

    @PutMapping
    public User updateUser(@Validated({/*User.OnUpdate.class,*/ Default.class}) @NotNull @RequestBody User user) {

        long t0 = System.nanoTime();
        log.debug("updateUser() – request id={}, login={}, email={}",
                user.getId(), user.getLogin(), user.getEmail());

        if (users.containsKey(user.getId())) {
            User oldUser = users.get(user.getId());

            oldUser.setEmail(user.getEmail());
            oldUser.setLogin(user.getLogin());
            oldUser.setName(user.getName());
            oldUser.setBirthday(user.getBirthday());

            long ms = (System.nanoTime() - t0) / 1_000_000;
            log.info("updateUser() – updated id={} in {} ms, updateUser={}", user.getId(), ms, oldUser);

            return oldUser;
        }
        log.warn("updateUser() – not found id={}", user.getId());
        throw new NotFoundException("User with id=" + user.getId() + " not found");
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        int size = users.size();
        log.debug("getAllUsers() – total={}", size);

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
