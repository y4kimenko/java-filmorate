package ru.yandex.practicum.filmorate.dal.events.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EventRowMapper implements RowMapper<Event> {

    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {

        long eventId = rs.getLong("event_id");
        Event.Operation operation = Event.Operation.valueOf(rs.getString("operation"));
        Event.EventType eventType = Event.EventType.valueOf(rs.getString("event_type"));
        long timestamp = rs.getLong("timestamp");
        long userId = rs.getLong("user_id");
        long entityId = rs.getLong("entity_id");

        Event event = Event.builder()
                .eventId(eventId)
                .operation(operation)
                .eventType(eventType)
                .timestamp(timestamp)
                .userId(userId)
                .entityId(entityId).build();


        return event;
    }

}
