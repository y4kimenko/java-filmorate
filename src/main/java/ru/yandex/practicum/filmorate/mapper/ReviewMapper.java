package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.review.request.ReviewRequestCreateDto;
import ru.yandex.practicum.filmorate.dto.review.request.ReviewRequestData;
import ru.yandex.practicum.filmorate.dto.review.request.ReviewRequestUpdateDto;
import ru.yandex.practicum.filmorate.dto.review.response.ReviewResponseDto;
import ru.yandex.practicum.filmorate.model.Review;

public class ReviewMapper {

    public static ReviewResponseDto toResponseDto(Review review) {
        return new ReviewResponseDto(
                review.getReviewId(),
                review.getUserId(),
                review.getFilmId(),
                review.getContent(),
                review.getIsPositive(),
                review.getUseful()
        );
    }

    public static Review toEntity(ReviewRequestCreateDto dto) {
        if (dto == null) return null;

        Review review = new Review();

        applyToEntity(dto, review);
        return review;
    }

    public static Review toEntity(ReviewRequestUpdateDto dto) {
        if (dto == null) return null;

        Review review = new Review();

        applyToEntity(dto, review);
        review.setReviewId(dto.reviewId());

        return review;
    }

    public static void applyToEntity(ReviewRequestData dto, Review review) {
        review.setUserId(dto.userId());
        review.setFilmId(dto.filmId());
        review.setContent(dto.content());
        review.setIsPositive(dto.isPositive());
    }
}
