package ru.yandex.practicum.filmorate.dal.events.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EventRowMapper implements RowMapper<Event> {

    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Event(
                rs.getLong("event_id"),
                rs.getLong("user_id"),
                Event.EventType.valueOf(rs.getString("event_type")),
                Event.Operation.valueOf(rs.getString("operation")),
                rs.getLong("entity_id"),
                rs.getLong("timestamp")
        );
    }
}
