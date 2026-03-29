package ru.yandex.practicum.filmorate.dal.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.film.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.enums.FilmsSearchBy;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Slf4j
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private static final String SELECT_FILM_BY_IDS = """
            SELECT id, title, mpa_id, description, release_date, duration
            FROM film
            WHERE id IN (:ids);""";
    private static final String SELECT_FILMS = """
            SELECT id, title, mpa_id, description, release_date, duration
            FROM film
            ORDER BY id;""";
    private static final String INSERT_FILM_WITH_MPA = """
            INSERT INTO film (title, mpa_id , description, release_date, duration)
            VALUES (:title, :mpa_id, :description, :release_date, :duration);""";
    private static final String INSERT_FILM = """
            INSERT INTO film (title , description, release_date, duration)
            VALUES (:title, :description, :release_date, :duration);""";
    private static final String UPDATE_FILM_WITH_MPA = """
            UPDATE film
            SET
              title = :title,
              mpa_id = :mpa_id,
              description = :description,
              release_date = :release_date,
              duration = :duration
            WHERE id = :id;""";

    private static final String UPDATE_FILM = """
            UPDATE film
            SET
              title = :title,
              description = :description,
              release_date = :release_date,
              duration = :duration
            WHERE id = :id;""";

    private static final String EXISTS_BY_ID = """
            SELECT count(id)
            FROM film
            WHERE id = :id""";

    private static final String GET_POPULAR_FILMS = """
            SELECT f.id, f.title, f.mpa_id, f.description, f.release_date, f.duration
            FROM film f
            LEFT OUTER JOIN user_film_likes l ON f.id = l.film_id
            GROUP BY f.id
            ORDER BY COUNT(l.user_id) DESC, f.id ASC
            LIMIT :max_size;""";

    private static final String DELETE_BY_ID = """
            DELETE FROM film
            WHERE id = :id
            """;

    private static final String GET_COMMON_FILMS = """
            SELECT f.id, f.title, f.mpa_id, f.description, f.release_date, f.duration
            FROM film f
            JOIN user_film_likes ul1 ON f.id = ul1.film_id
            JOIN user_film_likes ul2 ON f.id = ul2.film_id
            LEFT JOIN user_film_likes likes ON f.id = likes.film_id
            WHERE ul1.user_id = :userId
            AND ul2.user_id = :friendId
            GROUP BY f.id
            ORDER BY COUNT(likes.user_id) DESC, f.id ASC;
            """;

    private static final String GET_RECOMMENDATIONS = """
            SELECT f.id, f.title, f.mpa_id, f.description, f.release_date, f.duration
                    FROM film f
                    JOIN user_film_likes l ON f.id = l.film_id
                    WHERE l.user_id = (
                        SELECT l2.user_id
                        FROM user_film_likes l1
                        JOIN user_film_likes l2 ON l1.film_id = l2.film_id
                        WHERE l1.user_id = :userId
                          AND l2.user_id <> :userId
                        GROUP BY l2.user_id
                        ORDER BY COUNT(*) DESC, l2.user_id ASC
                        LIMIT 1
                    )
                      AND f.id NOT IN (
                        SELECT film_id
                        FROM user_film_likes
                        WHERE user_id = :userId
                    )
                    ORDER BY f.id;
            """;

    private static final String SEARCH_FILMS_BY_TITLE = """
            SELECT f.id, f.title, f.mpa_id, f.description, f.release_date, f.duration
            FROM film f
            WHERE""";

    private static final String GET_MOST_POPULAR_FILM = """
            SELECT f.id, f.title, f.mpa_id, f.description, f.release_date, f.duration,
            (SELECT COUNT(*) FROM user_film_likes ul WHERE ul.film_id = f.id) AS likes_count
            FROM film f
            WHERE (:year IS NULL OR EXTRACT(YEAR FROM f.release_date) = :year)
            AND (:genre_id IS NULL OR f.id IN (SELECT film_id FROM film_genres WHERE genre_id = :genre_id))
            ORDER BY likes_count DESC, f.id ASC
            LIMIT :count;
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Film save(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("title", film.getName())
                .addValue("description", film.getDescription())
                .addValue("release_date", film.getReleaseDate())
                .addValue("duration", film.getDuration());

        if (film.getMpa() != null) {
            params.addValue("mpa_id", film.getMpa().id());

            jdbcTemplate.update(INSERT_FILM_WITH_MPA,
                    params,
                    keyHolder,
                    new String[]{"id"}
            );
            log.info("Save a value in a table 'film' by ID={} with fields: title, mpa_id, description, release_date, duration",
                    film.getId());
        } else {
            // Добавление в таблицу film
            jdbcTemplate.update(INSERT_FILM,
                    params,
                    keyHolder,
                    new String[]{"id"}
            );
            log.info("Save a value in a table 'film' by ID={} with fields: title, description, release_date, duration",
                    film.getId());
        }

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("БД не вернула сгенерированный id");
        }
        film.setId(key.longValue());

        return film;
    }

    @Override
    public Film update(Film film) {

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", film.getId())
                .addValue("title", film.getName())
                .addValue("description", film.getDescription())
                .addValue("release_date", film.getReleaseDate())
                .addValue("duration", film.getDuration());


        if (film.getMpa() != null) {
            params.addValue("mpa_id", film.getMpa().id());

            jdbcTemplate.update(UPDATE_FILM_WITH_MPA, params);
            log.info("Updated a value in a table 'film' by ID={} with fields: title, mpa_id, description, release_date, duration",
                    film.getId());

        } else {
            jdbcTemplate.update(UPDATE_FILM, params);
            log.info("Updated a value in a table 'film' by ID={} with fields: title, description, release_date, duration",
                    film.getId());
        }

        return film;
    }

    @Override
    public List<Film> getAll() {
        log.info("(getAll) Retrieving all films in table 'film'");
        return jdbcTemplate.query(SELECT_FILMS,
                new MapSqlParameterSource(),
                new FilmRowMapper()
        );
    }

    @Override
    public Optional<Film> getById(long id) {
        log.info("(getById) Retrieving film by Id={}", id);

        return jdbcTemplate.query(SELECT_FILM_BY_IDS,
                new MapSqlParameterSource("ids", id),
                new FilmRowMapper()).stream().findFirst();
    }

    @Override
    public Set<Film> getByIds(Set<Long> filmIds) {
        log.info("(getByIds) Retrieving films in table 'film' with Ids={}", filmIds);

        if (filmIds == null || filmIds.isEmpty()) {
            return Collections.emptySet();
        }

        return new HashSet<>(jdbcTemplate.query(SELECT_FILM_BY_IDS,
                new MapSqlParameterSource("ids", filmIds),
                new FilmRowMapper()
        ));
    }

    @Override
    public boolean existsById(long id) {
        Long count = jdbcTemplate.queryForObject(
                EXISTS_BY_ID,
                new MapSqlParameterSource("id", id),
                Long.class
        );
        return count != null && count != 0;
    }

    @Override
    public int deleteById(long id) {
        int rows = jdbcTemplate.update(DELETE_BY_ID, new MapSqlParameterSource("id", id));
        log.debug("deleteById({}) - affected rows: {}", id, rows);

        return rows;
    }


    @Override
    public List<Film> getPopularFilms(long limit) {
        List<Film> res = jdbcTemplate.query(GET_POPULAR_FILMS,
                new MapSqlParameterSource("max_size", limit),
                new FilmRowMapper());
        log.info("(getPopularFilms) Request limit={}", limit);

        return res;
    }

    @Override
    public List<Film> getCommonFilms(long userId, long friendId) {
        List<Film> res = jdbcTemplate.query(
                GET_COMMON_FILMS,
                new MapSqlParameterSource()
                        .addValue("userId", userId)
                        .addValue("friendId", friendId),
                new FilmRowMapper()
        );

        log.info("(getCommonFilms) Request userId={}, friendId={}", userId, friendId);
        return res;
    }

    @Override
    public Map<Long, Film> searchByTitle(String title, List<FilmsSearchBy> searchBy) {
        log.info("(searchByTitle) Retrieving films in table 'film' with title={}", title);

        if (title == null || title.isEmpty()) {
            return Collections.emptyMap();
        }
        StringBuilder request = new StringBuilder(SEARCH_FILMS_BY_TITLE);

        if (searchBy.size() == 1) {
            switch (searchBy.getFirst()) {
                case FilmsSearchBy.FILM_NAME -> request.append(" LOWER(f.title) LIKE LOWER(:q)");
                case FilmsSearchBy.DIRECTOR -> request.append("""
                         f.id IN (
                              SELECT DISTINCT df.film_id
                              FROM film_directors df
                              WHERE df.director_id IN (
                                  SELECT d.id
                                  FROM director d
                                  WHERE LOWER(d.name) LIKE LOWER(:q)
                              )
                        )""");
            }
        } else {
            request.append(" 1 = 0");
            for (FilmsSearchBy s : searchBy) {
                switch (s) {
                    case FilmsSearchBy.FILM_NAME -> request.append(" OR LOWER(f.title) LIKE LOWER(:q)");
                    case FilmsSearchBy.DIRECTOR -> request.append("""
                             OR f.id IN (
                                  SELECT DISTINCT df.film_id
                                  FROM film_directors df
                                  WHERE df.director_id IN (
                                      SELECT d.id
                                      FROM director d
                                      WHERE LOWER(d.name) LIKE LOWER(:q)
                                  )
                            )""");
                }
            }
        }
        return jdbcTemplate.query(request.toString(),
                new MapSqlParameterSource("q", '%' + title + '%'),
                new FilmRowMapper()
        ).stream().collect(Collectors.toMap(Film::getId, f -> f));
    }

    @Override
    public List<Film> getMostPopularFilms(long count, Long genreId, Long year) {
        List<Film> res = jdbcTemplate.query(GET_MOST_POPULAR_FILM,
                new MapSqlParameterSource()
                        .addValue("year", year)
                        .addValue("genre_id", genreId)
                        .addValue("count", count),
                new FilmRowMapper()
        );
        log.info("getMostPopularFilms() – count={}, genreId={}, year={}", count, genreId, year);
        return res;
    }

    @Override
    public List<Film> getRecommendations(long userId) {
        List<Film> res = jdbcTemplate.query(
                GET_RECOMMENDATIONS,
                new MapSqlParameterSource("userId", userId),
                new FilmRowMapper()
        );

        log.info("getRecommendations() - request userId={}", userId);

        return res;
    }

}