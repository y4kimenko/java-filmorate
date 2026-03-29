package ru.yandex.practicum.filmorate.dal.director.directorsByFilm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Repository
@Slf4j
@RequiredArgsConstructor
public class DirectorByFilmDbStorage implements DirectorByFilmStorage {

    private static final String SELECT_DIRECTORS_BY_ID_FILM = """
            SELECT film_id, director_id
            from film_directors
            WHERE film_id IN (:film_ids);""";
    private static final String SELECT_FILMS_BY_ID_DIRECTOR = """
            SELECT film_id
            FROM film_directors
            WHERE director_id = :director_id;""";
    private static final String MERGE_DIRECTOR_FILM = """
            MERGE INTO film_directors (film_id, director_id)
            KEY (film_id, director_id)
            VALUES (:film_id, :director_id);""";
    private static final String DELETE_DIRECTORS_FILM = """
            DELETE FROM film_directors
            WHERE film_id = :film_id;""";
    private static final String DIRECTORS_BY_FILMS = """
            SELECT DISTINCT director_id
            FROM film_directors
            WHERE film_id IN (:film_ids);""";


    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void save(long filmId, Set<Long> directorIds) {
        SqlParameterSource[] batch = directorIds.stream()
                .map(dir -> new MapSqlParameterSource()
                        .addValue("director_id", dir)
                        .addValue("film_id", filmId))
                .toArray(SqlParameterSource[]::new);

        log.info("(save) Adding film's directors for film ID={} in table 'film_directors'", filmId);
        jdbcTemplate.batchUpdate(MERGE_DIRECTOR_FILM, batch);
    }

    @Override
    public void update(long filmId, Set<Long> directorIds) {
        log.info("(update) Deleting film's directors for film ID={} in table 'film_directors'", filmId);
        jdbcTemplate.update(DELETE_DIRECTORS_FILM,
                new MapSqlParameterSource()
                        .addValue("film_id", filmId));


        log.info("(update) Adding missing film's directors for film ID={} in table 'film_directors'", filmId);
        SqlParameterSource[] batch = directorIds.stream()
                .map(dir -> new MapSqlParameterSource()
                        .addValue("director_id", dir)
                        .addValue("film_id", filmId))
                .toArray(SqlParameterSource[]::new);
        jdbcTemplate.batchUpdate(MERGE_DIRECTOR_FILM, batch);
    }

    @Override
    public Map<Long, Set<Long>> getByFilmIds(Set<Long> filmIds) {
        if (filmIds == null || filmIds.isEmpty()) {
            return Collections.emptyMap();
        }
        log.info("(getByFilmIds) Retrieving film's directors for films IDs={} in table 'film_directors'", filmIds);
        return jdbcTemplate.query(
                SELECT_DIRECTORS_BY_ID_FILM,
                new MapSqlParameterSource("film_ids", filmIds),
                rs -> {
                    Map<Long, Set<Long>> map = new HashMap<>();
                    while (rs.next()) {
                        map.computeIfAbsent(rs.getLong("film_id"), k -> new HashSet<>())
                                .add(rs.getLong("director_id"));
                    }
                    return map;
                }
        );
    }

    @Override
    public Set<Long> getByDirectorId(Long directorId) {
        if (directorId == null) {
            return Collections.emptySet();
        }
        log.info("(getByDirectorId) Retrieving films director's for director ID={}", directorId);
        return new HashSet<>(jdbcTemplate.queryForList(
                SELECT_FILMS_BY_ID_DIRECTOR,
                new MapSqlParameterSource("director_id", directorId),
                Long.class
        ));
    }

    @Override
    public Set<Long> getUniqueDirectorIdsByFilmsIds(Set<Long> filmIds) {
        if (filmIds == null || filmIds.isEmpty()) {
            return Set.of();
        }

        return new HashSet<>(jdbcTemplate.queryForList(
                DIRECTORS_BY_FILMS,
                new MapSqlParameterSource("film_ids", filmIds),
                Long.class
        ));
    }
}
