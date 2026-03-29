package ru.yandex.practicum.filmorate.dal.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.review.mappers.ReviewRowMapper;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
@Slf4j
public class ReviewDbStorage implements ReviewStorage {
    private static final String INSERT_REVIEW = """
            INSERT INTO reviews (user_id, film_id, content, is_positive)
            VALUES (:user_id, :film_id, :content, :is_positive);""";

    private static final String UPDATE_REVIEW = """
            UPDATE reviews
            SET content=:content,
            is_positive=:is_positive
            WHERE id=:id;
            """;

    private static final String SELECT_USEFUL = """
            SELECT useful
            FROM reviews
            WHERE id=:id;
            """;

    private static final String DELETE_REVIEW = """
            DELETE
            FROM reviews
            WHERE id=:id;
            """;

    private static final String SELECT_REVIEWS_FILM_ID = """
            SELECT *
            FROM reviews
            WHERE film_id=:film_id
            LIMIT :count;
            """;

    private static final String SELECT_REVIEWS = """
            SELECT *
            FROM reviews
            LIMIT :count;
            """;

    private static final String ADD_LIKE_TO_REVIEW = """
            INSERT INTO review_reactions (review_id, user_id, reaction_type)
            VALUES (:review_id, :user_id, 'LIKE')
            """;

    private static final String ADD_DISLIKE_TO_REVIEW = """
            INSERT INTO review_reactions (review_id, user_id, reaction_type)
            VALUES (:review_id, :user_id, 'DISLIKE')
            """;

    private static final String SELECT_BY_ID = """
            SELECT *
            FROM reviews
            WHERE id = :id;
            """;

    private static final String INCREMENT_USEFUL = """
            UPDATE reviews
            SET useful = useful + 1
            WHERE id = :review_id;
            """;

    private static final String DECREMENT_USEFUL = """
            UPDATE reviews
            SET useful = useful - 1
            WHERE id = :review_id;
            """;

    private static final String DELETE_REACTION = """
            DELETE FROM review_reactions
            WHERE review_id = :review_id AND user_id = :user_id;
            """;

    private static final String SELECT_REACTION_TYPE = """
            SELECT reaction_type
            FROM review_reactions
            WHERE review_id = :review_id AND  user_id = :user_id;
            """;

    private static final String EXIST_REVIEW = """
            SELECT COUNT(*)
            FROM reviews
            WHERE id=:id;
            """;

    private static final String EXIST_REVIEW_BY_IDS = """
            SELECT COUNT(*)
            FROM reviews
            WHERE user_id=:user_id AND film_id=:film_id;
            """;

    private static final String EXIST_REACTION_BY_IDS = """
            SELECT COUNT(*)
            FROM review_reactions
            WHERE review_id =:review_id AND user_id=:user_id;
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Review save(Review review) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("user_id", review.getUserId())
                .addValue("film_id", review.getFilmId())
                .addValue("content", review.getContent())
                .addValue("is_positive", review.getIsPositive());

        jdbcTemplate.update(INSERT_REVIEW, params, keyHolder, new String[]{"id"});

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("БД не вернула сгенерированный id");
        }
        review.setReviewId(key.longValue());
        review.setUseful(0L);

        return review;
    }

    @Override
    public Review update(Review review) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("content", review.getContent())
                .addValue("is_positive", review.getIsPositive())
                .addValue("id", review.getReviewId());

        jdbcTemplate.update(UPDATE_REVIEW, params);
        log.info("Updated a value in a table 'reviews' ID={} with fields: user_id, film_id, content, is_positive," +
                        " useful",
                review.getReviewId());

        Long useful = jdbcTemplate.queryForObject(
                SELECT_USEFUL,
                new MapSqlParameterSource("id", review.getReviewId()),
                Long.class
        );
        review.setUseful(useful);

        return review;
    }

    @Override
    public int deleteReview(Long reviewId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", reviewId);

        return jdbcTemplate.update(DELETE_REVIEW, params);
    }

    @Override
    public List<Review> selectReviews(long count, Long filmId) {
        if (filmId != null) {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("film_id", filmId)
                    .addValue("count", count);

            return jdbcTemplate.query(SELECT_REVIEWS_FILM_ID, params, new ReviewRowMapper());
        } else {
            return jdbcTemplate.query(SELECT_REVIEWS,
                    new MapSqlParameterSource("count", count),
                    new ReviewRowMapper());
        }
    }

    @Override
    public void addLikeToReview(long reviewId, long userId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("review_id", reviewId)
                .addValue("user_id", userId);

        jdbcTemplate.update(ADD_LIKE_TO_REVIEW, params);
    }

    @Override
    public void addDislikeToReview(long reviewId, long userId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("review_id", reviewId)
                .addValue("user_id", userId);

        jdbcTemplate.update(ADD_DISLIKE_TO_REVIEW, params);
    }

    @Override
    public void deleteReaction(long reviewId, long userId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("review_id", reviewId)
                .addValue("user_id", userId);

        jdbcTemplate.update(DELETE_REACTION, params);
    }

    @Override
    public Optional<Review> findById(long reviewId) {
        List<Review> result = jdbcTemplate.query(
                SELECT_BY_ID,
                new MapSqlParameterSource("id", reviewId),
                new ReviewRowMapper()
        );
        return result.stream().findFirst();
    }

    @Override
    public String getReactionType(long reviewId, long userId) {

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("review_id", reviewId)
                .addValue("user_id", userId);

        return jdbcTemplate.query(SELECT_REACTION_TYPE, params,
                rs -> rs.next() ? rs.getString("reaction_type") : null);
    }

    @Override
    public void decrementUseful(long reviewId) {
        jdbcTemplate.update(DECREMENT_USEFUL, new MapSqlParameterSource("review_id", reviewId));
    }

    @Override
    public void incrementUseful(long reviewId) {
        jdbcTemplate.update(INCREMENT_USEFUL, new MapSqlParameterSource("review_id", reviewId));
    }

    @Override
    public boolean existById(long reviewId) {
        MapSqlParameterSource params = new MapSqlParameterSource("id", reviewId);

        Integer count = jdbcTemplate.queryForObject(
                EXIST_REVIEW,
                params,
                Integer.class
        );

        return count != null && count > 0;
    }

    @Override
    public boolean existByIds(long userId, long filmId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("user_id", userId)
                .addValue("film_id", filmId);

        Integer count = jdbcTemplate.queryForObject(
                EXIST_REVIEW_BY_IDS,
                params,
                Integer.class
        );

        return count != null && count > 0;
    }
}
