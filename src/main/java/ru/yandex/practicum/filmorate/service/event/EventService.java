package ru.yandex.practicum.filmorate.service.event;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventService {

    List<Event> getFeed(long userId);

}
