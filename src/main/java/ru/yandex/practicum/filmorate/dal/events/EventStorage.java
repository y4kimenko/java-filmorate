package ru.yandex.practicum.filmorate.dal.events;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage {

    void addEvent(Event event);

    List<Event> getUserFeed(long userId);

}
