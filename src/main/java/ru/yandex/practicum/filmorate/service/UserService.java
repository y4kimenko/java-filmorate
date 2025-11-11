package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class UserService {
    private final UserStorage storage;

    public User createUser(User user) {
        return storage.createUser(user);
    }

    public User updateUser(User user) {
        if (storage.getUserById(user.getId()).isPresent()) {
            return storage.updateUser(user);
        }
        throw new UserNotFoundException("Пользователь с id " + user.getId() + " не найден");
    }

    public Collection<User> getAllUsers() {
        return storage.getAllUsers();
    }

    public Set<User> getFriends(long userId) {
        return storage.getUserById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("Пользователь с id " + userId + " не найден")
                )
                .getFriends().stream()
                .map(storage::getUserById)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
    }

    public void addFriend(long userId, long friendId) {
        User user = storage.getUserById(userId).orElseThrow(() ->
                new UserNotFoundException("Пользователь с id " + userId + " не найден")
        );

        User friend = storage.getUserById(friendId).orElseThrow(() ->
                new UserNotFoundException("Пользователь с id " + friendId + " не найден")
        );

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void removeFriend(long userId, long friendId) {
        User user = storage.getUserById(userId).orElseThrow(() ->
                new UserNotFoundException("Пользователь с id " + userId + " не найден")
        );

        User friend = storage.getUserById(friendId).orElseThrow(() ->
                new UserNotFoundException("Пользователь с id " + friendId + " не найден")
        );

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public Set<User> getMutualFriends(long userId, long friendId) {
        Set<Long> friendsUser = storage.getUserById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("Пользователь с id " + userId + " не найден")
                )
                .getFriends();

        return storage.getUserById(friendId).orElseThrow(() ->
                        new UserNotFoundException("Пользователь с id " + friendId + " не найден")
                )
                .getFriends().stream()
                .filter(x -> !friendsUser.add(x))
                .map(storage::getUserById)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
    }


}
