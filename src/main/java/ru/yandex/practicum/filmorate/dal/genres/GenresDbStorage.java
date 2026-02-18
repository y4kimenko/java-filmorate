package ru.yandex.practicum.filmorate.dal.genres;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.genre.response.GenreResponseDto;


import java.util.*;


@Repository
@RequiredArgsConstructor
public class GenresDbStorage implements GenresStorage{
    private static final String SELECT_ALL_GENRE = """
        SELECT id, name
        FROM genres;""";

    private static final String SELECT_GENRE_BY_IDS = """
        SELECT id, name
        FROM genres
        WHERE id IN (:ids)
        ORDER BY id;""";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Map<Long, GenreResponseDto> getAll() {
        return jdbcTemplate.query(SELECT_ALL_GENRE,
                        new MapSqlParameterSource(),
                rs -> {
                    Map<Long, GenreResponseDto> result = new HashMap<>();
                    while (rs.next()) {
                        long id = rs.getLong("id");
                        String name = rs.getString("name");
                        result.put(id, new GenreResponseDto(id, name));
                    }
                    return result;
                }
        );
    }

    @Override
    public Optional<GenreResponseDto> getById(long id) {
        return jdbcTemplate.query(SELECT_GENRE_BY_IDS,
                new MapSqlParameterSource("ids", id),
                new DataClassRowMapper<>(GenreResponseDto.class)
        ).stream().findFirst();
    }

    @Override
    public LinkedHashSet<GenreResponseDto> getByIds(Set<Long> ids) {
        return new LinkedHashSet<>(jdbcTemplate.query(SELECT_GENRE_BY_IDS,
                new MapSqlParameterSource("ids", ids),
                new DataClassRowMapper<>(GenreResponseDto.class)
        ));
    }
}
