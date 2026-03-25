package ru.yandex.practicum.filmorate.dal.genre.genres;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


@Repository
@RequiredArgsConstructor
public class GenresDbStorage implements GenresStorage {
    private static final String SELECT_ALL_GENRE = """
            SELECT id, name
            FROM genres
            ORDER BY id;""";

    private static final String SELECT_GENRE_BY_IDS = """
            SELECT id, name
            FROM genres
            WHERE id IN (:ids)
            ORDER BY id;""";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Map<Long, Genre> getAll() {
        return jdbcTemplate.query(SELECT_ALL_GENRE,
                new MapSqlParameterSource(),
                rs -> {
                    Map<Long, Genre> result = new HashMap<>();
                    while (rs.next()) {
                        long id = rs.getLong("id");
                        String name = rs.getString("name");
                        result.put(id, new Genre(id, name));
                    }
                    return result;
                }
        );
    }

    @Override
    public Optional<Genre> getById(long id) {
        return jdbcTemplate.query(SELECT_GENRE_BY_IDS,
                new MapSqlParameterSource("ids", id),
                new DataClassRowMapper<>(Genre.class)
        ).stream().findFirst();
    }

    @Override
    public Map<Long, Genre> getByIds(Set<Long> ids) {
        return new HashMap<>(jdbcTemplate.query(SELECT_GENRE_BY_IDS,
                        new MapSqlParameterSource("ids", ids),
                        new DataClassRowMapper<>(Genre.class)).stream()
                .collect(Collectors.toMap(
                        Genre::id,
                        Function.identity()
                ))
        );
    }
}
