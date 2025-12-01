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
        requireUser(user.getId());
        return storage.updateUser(user);
    }

    public Collection<User> getAllUsers() {
        return storage.getAllUsers();
    }

    public Set<User> getFriends(long userId) {
        return requireUser(userId)
                .getFriends().stream()
                .map(storage::getUserById)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
    }

    public void addFriend(long userId, long friendId) {
        requireUser(userId).getFriends().add(friendId);
        requireUser(friendId).getFriends().add(userId);
    }

    public void removeFriend(long userId, long friendId) {
        requireUser(userId).getFriends().remove(friendId);
        requireUser(friendId).getFriends().remove(userId);
    }

    public Set<User> getMutualFriends(long userId, long friendId) {
        Set<Long> friendsUser = requireUser(userId).getFriends();

        return requireUser(friendId)
                .getFriends().stream()
                .filter(x -> !friendsUser.add(x))
                .map(storage::getUserById)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
    }

    private User requireUser(long userId) {
        return storage.getUserById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("Пользователь с id " + userId + " не найден")
                );
    }

}
