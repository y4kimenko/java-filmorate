package ru.yandex.practicum.filmorate.dal.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private static final String SELECT_ALL_MPA = """
            SELECT id, name
            FROM mpa;""";

    private static final String SELECT_MPA_BY_ID = """
            SELECT id, name
            FROM mpa
            WHERE id = :id;""";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Map<Long, Mpa> getAll() {
        return jdbcTemplate.query(SELECT_ALL_MPA,
                new MapSqlParameterSource(),
                rs -> {
                    Map<Long, Mpa> result = new HashMap<>();
                    while (rs.next()) {
                        long id = rs.getLong("id");
                        String name = rs.getString("name");
                        result.put(id, new Mpa(id, name));
                    }
                    return result;
                }
        );
    }

    @Override
    public Optional<Mpa> getById(Long id) {
        return jdbcTemplate.query(SELECT_MPA_BY_ID,
                new MapSqlParameterSource("id", id),
                new DataClassRowMapper<>(Mpa.class)
        ).stream().findFirst();
    }
}
