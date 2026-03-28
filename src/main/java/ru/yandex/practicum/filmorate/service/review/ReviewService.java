package ru.yandex.practicum.filmorate.service.review;

import ru.yandex.practicum.filmorate.dto.review.request.ReviewRequestCreateDto;
import ru.yandex.practicum.filmorate.dto.review.request.ReviewRequestUpdateDto;
import ru.yandex.practicum.filmorate.dto.review.response.ReviewResponseDto;

import java.util.List;

public interface ReviewService {
    ReviewResponseDto createReview(ReviewRequestCreateDto review);

    ReviewResponseDto updateReview(ReviewRequestUpdateDto dto);

    void deleteReview(Long reviewId);

    List<ReviewResponseDto> selectReviews(long count, Long filmId);

    void addLikeToReview(long reviewId, long userId);

    void addDislikeToReview(long reviewId, long userId);

    void deleteReaction(long reviewId, long userId);

    ReviewResponseDto getReviewById(Long id);
}
