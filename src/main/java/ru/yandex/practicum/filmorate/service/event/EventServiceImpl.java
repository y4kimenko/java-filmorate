package ru.yandex.practicum.filmorate.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.events.EventStorage;
import ru.yandex.practicum.filmorate.dal.user.UserStorage;
import ru.yandex.practicum.filmorate.exception.notFound.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventStorage eventStorage;
    private final UserStorage userStorage;

    @Override
    public List<Event> getFeed(long userId) {
        if (!userStorage.existsById(userId)) {
            throw new UserNotFoundException("Пользователь с айди " + userId + " не найден");
        }
        return eventStorage.getUserFeed(userId);
    }
}
