package ru.yandex.practicum.filmorate.dal.events;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.events.mappers.EventRowMapper;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class EventDbStorage implements EventStorage {

    private static final String INSERT_EVENT = """
                INSERT INTO event_feed (user_id, event_type, operation, entity_id, timestamp)
                VALUES (:userId, :eventType, :operation, :entityId, :timestamp)
            """;

    private static final String GET_FEED = """
                SELECT *
                FROM event_feed
                WHERE user_id = :userId
                ORDER BY timestamp ASC
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void addEvent(Event event) {
        jdbcTemplate.update(INSERT_EVENT, new MapSqlParameterSource()
                .addValue("userId", event.userId())
                .addValue("eventType", event.eventType().name())   // важно!
                .addValue("operation", event.operation().name())   // важно!
                .addValue("entityId", event.entityId())
                .addValue("timestamp", event.timestamp()));
    }

    @Override
    public List<Event> getUserFeed(long userId) {
        return jdbcTemplate.query(GET_FEED, new MapSqlParameterSource("userId", userId), new EventRowMapper());
    }
}
