package ru.yandex.practicum.filmorate.dal.likes;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
@Slf4j
public class LikesDbStorage implements LikesStorage {
    private static final String MERGE_USER_LIKES_FILMS = """
            MERGE INTO user_film_likes (user_id, film_id)
            KEY (user_id, film_id)
            VALUES (:user_id, :film_id);""";

    private static final String DELETE_LIKE_FILM_BY_USER = """
            DELETE FROM user_film_likes
            WHERE user_id = :user_id
            AND film_id = :film_id;""";

    private static final String FILMS_SORTED_BY_LIKES = """
            SELECT f.id AS film_id,
                    COUNT(ufl.user_id) AS likes_count
            FROM film f
            LEFT JOIN user_film_likes ufl ON ufl.film_id = f.id
            WHERE f.id IN (:film_ids)
            GROUP BY f.id
            ORDER BY likes_count DESC, f.id;""";


    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public boolean addLikeFilmByUser(long userId, long filmId) {

        log.info("(addLikeFilmByUser) Adding favorite user's films with userID={} in table 'user_film_likes'", userId);
        int res = jdbcTemplate.update(MERGE_USER_LIKES_FILMS,
                new MapSqlParameterSource()
                        .addValue("user_id", userId)
                        .addValue("film_id", filmId)
        );
        return res > 0;
    }

    @Override
    public boolean removeLikeFilmByUser(long userId, long filmId) {
        long res = jdbcTemplate.update(DELETE_LIKE_FILM_BY_USER,
                new MapSqlParameterSource()
                        .addValue("user_id", userId)
                        .addValue("film_id", filmId)
        );
        log.info("(removeLikeFilmByUser) Delete filmId={} likes for userId={} from the table 'user_film_likes'", filmId, userId);
        return res > 0;
    }

    @Override
    public List<Long> getFilmsSortedByLikes(Set<Long> filmsIds) {
        log.info("(getFilmsSortedByLikes) Retrieving sorted films by likes in table 'user_film_likes'");
        return jdbcTemplate.query(FILMS_SORTED_BY_LIKES,
                new MapSqlParameterSource("film_ids", filmsIds),
                (rs, rowNum) -> rs.getLong("id")
        );
    }
}
