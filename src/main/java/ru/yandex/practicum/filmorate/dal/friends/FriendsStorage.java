package ru.yandex.practicum.filmorate.dal.friends;


import ru.yandex.practicum.filmorate.dto.friend.FriendShipsDto;

import java.util.Optional;
import java.util.Set;

public interface FriendsStorage {
    void addFriend(long requester, long addressee);

    Optional<FriendShipsDto> getFriendship(long userId, long friendId);

    void updateFriendships(FriendShipsDto dto);

    void deleteFriendships(long requester, long addressee);

    Set<Long> getFriends(long userId);
}
