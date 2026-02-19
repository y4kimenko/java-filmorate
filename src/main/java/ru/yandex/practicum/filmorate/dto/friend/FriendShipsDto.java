package ru.yandex.practicum.filmorate.dto.friend;

public record FriendShipsDto(
        long requesterId,
        long addresseeId,
        boolean addresseeDeleted,
        boolean status
) {
}
