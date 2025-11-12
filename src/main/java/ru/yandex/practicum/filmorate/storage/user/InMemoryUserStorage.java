package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Log4j2
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User createUser(User user) {
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

    @Override
    public User updateUser(User user) {
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

    @Override
    public Collection<User> getAllUsers() {
        int size = users.size();
        log.debug("getAllUsers() – total={}", size);

        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(users.get(id));
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
