package ru.yandex.practicum.filmorate.dal.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.film.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.user.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFriendsException;
import ru.yandex.practicum.filmorate.exception.notFound.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Primary
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


    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final UserDbStorage userDbStorage;

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
    public LinkedHashMap<Long, Film> getAll() {
        return jdbcTemplate.queryForStream(SELECT_FILMS,
                new MapSqlParameterSource(),
                new FilmRowMapper()).collect(
                Collectors.toMap(
                        Film::getId,             // Ключ
                        film -> film,
                        (prev, next) -> next, // Если ключи совпали, берем новый (или старый)
                        LinkedHashMap::new)    // Значение (сам объект)
        );
    }

    @Override
    public Optional<Film> getById(long id) {
        log.info("getById() – request FilmId={}", id);

        return jdbcTemplate.query(SELECT_FILM_BY_IDS,
                new MapSqlParameterSource("ids", id),
                new FilmRowMapper()).stream().findFirst();
    }

    @Override
    public List<Film> getPopularFilms(long limit) {
        List<Film> res = jdbcTemplate.query(GET_POPULAR_FILMS,
                new MapSqlParameterSource("max_size", limit),
                new FilmRowMapper());
        log.info("getPopularFilms() – request limit={}", limit);

        return res;
    }

    @Override
    public int countLikesByFilmId(Long filmId) {
        MapSqlParameterSource params = new MapSqlParameterSource("filmId", filmId);
        return jdbcTemplate.queryForObject(
                "SELECT COALESCE(COUNT(*), 0) FROM user_film_likes WHERE film_id = :filmId",
                params,
                Integer.class
        );
    }

    @Override
    public List<Film> getCommonFilmsBetweenUsers(long userId, long friendId) {
        User firstUser = userDbStorage.getById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + userId + " не найден"));
        User secondUser = userDbStorage.getById(friendId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + userId + " не найден"));

        if (!firstUser.getFriends().contains(friendId)) {
            throw new NotFriendsException("Пользователи " + userId + " и " + friendId + " не друзья");
        }

        Set<Long> commonLikedFilmIds = new HashSet<>(firstUser.getLikedFilm());
        commonLikedFilmIds.retainAll(secondUser.getLikedFilm());

        return getAll().values().stream()
                .filter(film -> commonLikedFilmIds.contains(film.getId()))
                .sorted(Comparator.comparingInt((Film film) -> countLikesByFilmId(film.getId())).reversed())
                .collect(Collectors.toList());

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
}
