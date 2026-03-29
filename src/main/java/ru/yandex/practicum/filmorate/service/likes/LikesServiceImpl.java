package ru.yandex.practicum.filmorate.service.likes;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.events.EventStorage;
import ru.yandex.practicum.filmorate.dal.film.FilmStorage;
import ru.yandex.practicum.filmorate.dal.likes.LikesStorage;
import ru.yandex.practicum.filmorate.dal.user.UserStorage;
import ru.yandex.practicum.filmorate.exception.notFound.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.notFound.LikeNotFoundException;
import ru.yandex.practicum.filmorate.exception.notFound.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;

@Service
@RequiredArgsConstructor
public class LikesServiceImpl implements LikesService {
    private final LikesStorage likesStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final EventStorage eventStorage;

    @Override
    public void addUserLike(long filmId, long userId) {
        if (!userStorage.existsById(userId))
            throw new UserNotFoundException("User c id=" + userId + " не найден.");
        if (!filmStorage.existsById(filmId))
            throw new FilmNotFoundException("Film c id=" + filmId + " не найден.");

        likesStorage.addLikeFilmByUser(userId, filmId);

        eventStorage.addEvent(
                Event.of(
                        userId,
                        Event.EventType.LIKE,
                        Event.Operation.ADD,
                        filmId
                )
        );
    }

    @Override
    public void removeUserLike(long filmId, long userId) {
        if (!userStorage.existsById(userId))
            throw new UserNotFoundException("User c id=" + userId + " не найден.");
        if (!filmStorage.existsById(filmId))
            throw new FilmNotFoundException("Film c id=" + filmId + " не найден.");

        if (!likesStorage.removeLikeFilmByUser(userId, filmId))
            throw new LikeNotFoundException("User c id=" + userId + " не ставил лайк фильму с id=" + filmId);

        eventStorage.addEvent(
                Event.of(
                        userId,
                        Event.EventType.LIKE,
                        Event.Operation.REMOVE,
                        filmId
                )
        );
    }

}
