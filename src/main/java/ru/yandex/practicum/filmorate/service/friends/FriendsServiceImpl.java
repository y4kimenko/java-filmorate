package ru.yandex.practicum.filmorate.service.friends;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.events.EventStorage;
import ru.yandex.practicum.filmorate.dal.friends.FriendsStorage;
import ru.yandex.practicum.filmorate.dal.user.UserStorage;
import ru.yandex.practicum.filmorate.dto.friend.FriendShipsDto;
import ru.yandex.practicum.filmorate.dto.user.response.UserResponseDto;
import ru.yandex.practicum.filmorate.exception.notFound.UserNotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendsServiceImpl implements FriendsService {
    private final UserStorage userStorage;
    private final FriendsStorage friendsStorage;
    private final EventStorage eventStorage;

    @Override
    public void addFriend(long requester, long addressee) {
        if (!userStorage.existsById(requester)) {
            throw new UserNotFoundException("User с id=" + requester + " не найден.");
        }

        if (!userStorage.existsById(addressee)) {
            throw new UserNotFoundException("User с id=" + addressee + " не найден.");
        }

        friendsStorage.addFriend(requester, addressee);
        eventStorage.addEvent(
                Event.of(
                        requester,
                        Event.EventType.FRIEND,
                        Event.Operation.ADD,
                        addressee
                )
        );
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        if (!userStorage.existsById(userId)) {
            throw new UserNotFoundException("User с id=" + userId + " не найден.");
        }

        if (!userStorage.existsById(friendId)) {
            throw new UserNotFoundException("User с id=" + friendId + " не найден.");
        }

        FriendShipsDto dto = friendsStorage.getFriendship(userId, friendId).orElse(null);

        if (dto == null) return;

        if (userId == dto.requesterId()) {
            friendsStorage.deleteFriendships(userId, friendId);
        } else {
            friendsStorage.updateFriendships(new FriendShipsDto(friendId, userId, true, true));
        }

        eventStorage.addEvent(
                Event.of(
                        userId,
                        Event.EventType.FRIEND,
                        Event.Operation.ADD,
                        friendId
                )
        );

    }

    @Override
    public List<UserResponseDto> getFriends(long userId) {
        if (!userStorage.existsById(userId)) {
            throw new UserNotFoundException("User с id=" + userId + " не найден.");
        }

        return userStorage.getByIds(friendsStorage.getFriends(userId)).stream()
                .map(UserMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<UserResponseDto> getMutualFriends(long userId, long friendId) {
        if (!userStorage.existsById(userId)) {
            throw new UserNotFoundException("User с id=" + userId + " не найден.");
        }

        if (!userStorage.existsById(friendId)) {
            throw new UserNotFoundException("User с id=" + friendId + " не найден.");
        }

        Set<Long> userFriends = friendsStorage.getFriends(userId);

        Set<Long> mutualFriends = friendsStorage.getFriends(friendId).stream()
                .filter(userFriends::contains)
                .collect(Collectors.toSet());

        return userStorage.getByIds(mutualFriends).stream()
                .map(UserMapper::toResponseDto)
                .toList();
    }


}
