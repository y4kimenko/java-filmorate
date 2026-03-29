package ru.yandex.practicum.filmorate.dal.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {

    Review save(Review review);

    Review update(Review review);

    int deleteReview(Long reviewId);

    List<Review> selectReviews(long count, Long filmId);

    void addLikeToReview(long reviewId, long userId);

    boolean existById(long reviewId);

    boolean existByIds(long userId, long filmId);

    void deleteReaction(long reviewId, long userId);

    Boolean getReactionType(long reviewId, long userId);

    void decrementUseful(long reviewId);

    void incrementUseful(long reviewId);

    void addDislikeToReview(long reviewId, long userId);

    Optional<Review> findById(long reviewId);
}
