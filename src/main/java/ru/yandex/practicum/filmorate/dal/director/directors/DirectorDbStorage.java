package ru.yandex.practicum.filmorate.dal.director.directors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private static final String SELECT_ALL_DIRECTORS = """
            SELECT id, name
            FROM director
            ORDER BY id;""";

    private static final String DELETE_BY_ID = """
            DELETE FROM director
            WHERE id = :id;""";

    private static final String SELECT_DIRECTOR_BY_ID = """
            SELECT id, name
            FROM director
            WHERE id = :id;""";

    private static final String INSERT_DIRECTOR = """
            INSERT INTO director (name)
            VALUES (:name);""";

    private static final String UPDATE_DIRECTOR = """
            UPDATE film
            SET name = :name
            WHERE id = :id;""";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Director save(Director dir) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(INSERT_DIRECTOR,
                new MapSqlParameterSource()
                        .addValue("name", dir.getName()),
                keyHolder,
                new String[]{"id"}
        );

        log.info("Save a value in a table 'director' by ID={} with fields: name",
                dir.getId());

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("БД не вернула сгенерированный id");
        }

        dir.setId(key.longValue());

        return dir;
    }

    @Override
    public Director update(Director dir) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", dir.getId())
                .addValue("name", dir.getName());

        jdbcTemplate.update(UPDATE_DIRECTOR, params);
        log.info("Updated a value in a table 'director' by ID={} with fields: name",
                dir.getId());

        return dir;
    }

    @Override
    public Map<Long, Director> getAll() {
        return jdbcTemplate.query(SELECT_ALL_DIRECTORS,
                new MapSqlParameterSource(),
                rs -> {
                    Map<Long, Director> result = new HashMap<>();
                    while (rs.next()) {
                        Director d = new Director();
                        d.setId(rs.getLong("id"));
                        d.setName(rs.getString("name"));
                        result.put(d.getId(), d);
                    }
                    return result;
                }
        );
    }

    @Override
    public Optional<Director> getById(Long id) {
        return jdbcTemplate.query(SELECT_DIRECTOR_BY_ID,
                new MapSqlParameterSource("id", id),
                new DataClassRowMapper<>(Director.class)
        ).stream().findFirst();
    }

    @Override
    public int deleteById(long id) {
        int rows = jdbcTemplate.update(DELETE_BY_ID, new MapSqlParameterSource("id", id));
        log.debug("deleteById({}) - affected rows: {}", id, rows);

        return rows;
    }


}
