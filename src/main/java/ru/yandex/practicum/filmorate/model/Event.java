package ru.yandex.practicum.filmorate.model;

public record Event(
        long eventId,
        long userId,
        EventType eventType,
        Operation operation,
        long entityId,
        long timestamp
) {

    public enum EventType {
        LIKE,
        REVIEW,
        FRIEND
    }

    public enum Operation {
        REMOVE,
        ADD,
        UPDATE
    }

    public static Event of(long userId,
                           EventType eventType,
                           Operation operation,
                           long entityId) {
        return new Event(
                0, // eventId генерится в БД
                userId,
                eventType,
                operation,
                entityId,
                System.currentTimeMillis()
        );
    }

}