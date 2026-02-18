package ru.yandex.practicum.filmorate.dal.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.mpa.response.MpaResponseDto;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    public Set<MpaResponseDto> getAll() {
        return jdbcTemplate.queryForStream(SELECT_ALL_MPA,
                                            new MapSqlParameterSource(),
                                            new DataClassRowMapper<>(MpaResponseDto.class))
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<MpaResponseDto> getById(Long id) {
        return jdbcTemplate.query(SELECT_MPA_BY_ID,
                                           new MapSqlParameterSource("id", id),
                                           new DataClassRowMapper<>(MpaResponseDto.class)
        ).stream().findFirst();
    }
}
