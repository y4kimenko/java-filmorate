package ru.yandex.practicum.filmorate.service.likes;

public interface LikesService {
    void addUserLike(long filmId, long userId);

    void removeUserLike(long filmId, long userId);
}
