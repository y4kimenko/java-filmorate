package ru.yandex.practicum.filmorate.dal.genresByFilms;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Slf4j
@RequiredArgsConstructor
public class GenresByFilmsDbStorage implements GenresByFilmsStorage {
    private static final String SELECT_FILM_GENRES = """
            SELECT film_id, genre_id
            FROM film_genres
            ORDER BY film_id, genre_id;""";
    private static final String SELECT_FILMS_GENRES_BY_ID = """
            SELECT film_id, genre_id
            FROM film_genres
            WHERE film_id IN (:film_id);""";
    private static final String MERGE_GENRE_FILM = """
            MERGE INTO film_genres (film_id, genre_id)
            KEY (film_id, genre_id)
            VALUES (:film_id, :genre_id);""";
    private static final String DELETE_GENRES_FILM = """
            DELETE FROM film_genres
            WHERE film_id = :film_id
            AND genre_id NOT IN (:genreIds);""";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void save(long filmId, Set<Long> genres) {
        SqlParameterSource[] batch = genres.stream()
                .map(genre -> new MapSqlParameterSource()
                        .addValue("genre_id", genre)
                        .addValue("film_id", filmId))
                .toArray(SqlParameterSource[]::new);

        log.info("Adding film's genres for film ID={} in table 'genres_film'", filmId);
        jdbcTemplate.batchUpdate(MERGE_GENRE_FILM, batch);
    }

    @Override
    public void update(long filmId, Set<Long> genres) {
        log.info("Deleting film's genres for film ID={} in table 'genres_film'", filmId);
        jdbcTemplate.update(DELETE_GENRES_FILM,
                new MapSqlParameterSource()
                        .addValue("film_id", filmId)
                        .addValue("genreIds", genres));


        log.info("Adding missing film's genres for film ID={} in table 'genres_film'", filmId);
        SqlParameterSource[] batch = genres.stream()
                .map(genre -> new MapSqlParameterSource()
                        .addValue("genre_id", genre)
                        .addValue("film_id", filmId))
                .toArray(SqlParameterSource[]::new);
        jdbcTemplate.batchUpdate(MERGE_GENRE_FILM, batch);
    }

    @Override
    public Map<Long, Set<Long>> getAll() {
        return jdbcTemplate.query(
                SELECT_FILM_GENRES,
                new MapSqlParameterSource(),
                rs -> {
                    Map<Long, Set<Long>> map = new HashMap<>();
                    while (rs.next()) {
                        map.computeIfAbsent(rs.getLong("film_id"), k -> new LinkedHashSet<>())
                                .add(rs.getLong("genre_id"));
                    }
                    return map;
                }
        );
    }

    @Override
    public Map<Long, Set<Long>> getByfilmIds(Set<Long> filmIds) {
        return jdbcTemplate.query(
                SELECT_FILMS_GENRES_BY_ID,
                new MapSqlParameterSource("film_id", filmIds),
                rs -> {
                    Map<Long, Set<Long>> map = new HashMap<>();
                    while (rs.next()) {
                        map.computeIfAbsent(rs.getLong("film_id"), k -> new LinkedHashSet<>())
                                .add(rs.getLong("genre_id"));
                    }
                    return map;
                }
        );
    }

    @Override
    public Set<Long> getByFilmId(Long filmId) {
        return new HashSet<>(jdbcTemplate.query(
                SELECT_FILMS_GENRES_BY_ID,
                new MapSqlParameterSource("film_id", filmId),
                (rs, rowNum) -> rs.getLong("genre_id")
        ));
    }

}
