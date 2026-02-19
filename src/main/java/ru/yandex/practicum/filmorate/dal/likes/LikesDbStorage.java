package ru.yandex.practicum.filmorate.dal.likes;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

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


    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public boolean addLikeFilmByUser(long userId, long filmId) {

        log.info("Adding favorite user's films with userID={} in table 'user_film_likes'", userId);
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
        log.info("Delete filmId={} likes for userId={} from the table 'user_film_likes'", filmId, userId);
        return res > 0;
    }
}
