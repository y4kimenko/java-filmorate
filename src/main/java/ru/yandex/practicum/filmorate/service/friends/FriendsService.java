package ru.yandex.practicum.filmorate.service.friends;

import ru.yandex.practicum.filmorate.dto.user.response.UserResponseDto;

import java.util.List;

public interface FriendsService {
    void addFriend(long requester, long addressee);

    void removeFriend(long user, long friend);

    List<UserResponseDto> getFriends(long userId);

    List<UserResponseDto> getMutualFriends(long userId, long friendId);
}
