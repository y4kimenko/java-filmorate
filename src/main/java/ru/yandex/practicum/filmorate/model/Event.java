package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Event {
    private final long eventId;
    private final long userId;
    private final EventType eventType;
    private final Operation operation;
    private final long entityId;
    private final long timestamp;

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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private long eventId;
        private long userId;
        private EventType eventType;
        private Operation operation;
        private long entityId;
        private long timestamp;

        public Builder eventId(long eventId) {
            this.eventId = eventId;
            return this;
        }

        public Builder userId(long userId) {
            this.userId = userId;
            return this;
        }

        public Builder eventType(EventType eventType) {
            this.eventType = eventType;
            return this;
        }

        public Builder operation(Operation operation) {
            this.operation = operation;
            return this;
        }

        public Builder entityId(long entityId) {
            this.entityId = entityId;
            return this;
        }

        public Builder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Event build() {
            return new Event(eventId, userId, eventType, operation, entityId, timestamp);
        }

    }

}