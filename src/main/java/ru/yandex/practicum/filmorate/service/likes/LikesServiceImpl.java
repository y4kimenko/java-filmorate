package ru.yandex.practicum.filmorate.service.likes;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.film.FilmStorage;
import ru.yandex.practicum.filmorate.dal.likes.LikesStorage;
import ru.yandex.practicum.filmorate.dal.user.UserStorage;
import ru.yandex.practicum.filmorate.exception.notFound.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.notFound.LikeNotFoundException;
import ru.yandex.practicum.filmorate.exception.notFound.UserNotFoundException;

@Service
@RequiredArgsConstructor
public class LikesServiceImpl implements LikesService {
    private final LikesStorage likesStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Override
    public void addUserLike(long filmId, long userId) {
        if (!userStorage.existsById(userId))
            throw new UserNotFoundException("User c id=" + userId + " не найден.");
        if (!filmStorage.existsById(filmId))
            throw new FilmNotFoundException("Film c id=" + filmId + " не найден.");

        likesStorage.addLikeFilmByUser(userId, filmId);
    }

    @Override
    public void removeUserLike(long filmId, long userId) {
        if (!userStorage.existsById(userId))
            throw new UserNotFoundException("User c id=" + userId + " не найден.");
        if (!filmStorage.existsById(filmId))
            throw new FilmNotFoundException("Film c id=" + filmId + " не найден.");

        if (!likesStorage.removeLikeFilmByUser(userId, filmId))
            throw new LikeNotFoundException("User c id=" + userId + " не ставил лай фильму с id=" + filmId);
    }
}
