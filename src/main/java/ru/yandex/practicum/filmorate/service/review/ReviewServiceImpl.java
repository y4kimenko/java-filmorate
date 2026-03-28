package ru.yandex.practicum.filmorate.service.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dal.review.ReviewStorage;
import ru.yandex.practicum.filmorate.dal.user.UserStorage;
import ru.yandex.practicum.filmorate.dto.review.request.ReviewRequestCreateDto;
import ru.yandex.practicum.filmorate.dto.review.request.ReviewRequestUpdateDto;
import ru.yandex.practicum.filmorate.dto.review.response.ReviewResponseDto;
import ru.yandex.practicum.filmorate.exception.DuplicateReviewException;
import ru.yandex.practicum.filmorate.exception.notFound.ReactionNotFound;
import ru.yandex.practicum.filmorate.exception.notFound.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.notFound.UserNotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {
    private final ReviewStorage reviewStorage;
    private final ReviewMapper reviewMapper;
    private final UserStorage userStorage;

    @Override
    @Transactional
    public ReviewResponseDto createReview(ReviewRequestCreateDto dto) {
        if (reviewStorage.existByIds(dto.userId(), dto.filmId())) {
            throw new DuplicateReviewException("Отзыв уже существует");
        }

        Review res = reviewStorage.save(reviewMapper.toEntity(dto));

        log.info("save() – reviewId={}, userId={}, filmId={}, content={}, isPositive={}, useful={}",
                res.getReviewId(), res.getUserId(), res.getFilmId(),
                res.getContent(), res.getIsPositive(), res.getUseful());

        return reviewMapper.toResponseDto(res);
    }

    @Override
    @Transactional
    public ReviewResponseDto updateReview(ReviewRequestUpdateDto dto) {
        if (!reviewStorage.existById(dto.reviewId())) {
            throw new ReviewNotFoundException("Отзыв не найден");
        }
        Review res = reviewStorage.update(reviewMapper.toEntity(dto));

        return reviewMapper.toResponseDto(res);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        if (reviewStorage.deleteReview(reviewId) == 0) {
            throw new ReviewNotFoundException("Отзыв не найден");
        }
    }

    @Override
    @Transactional
    public List<ReviewResponseDto> selectReviews(long count, Long filmId) {

        return reviewStorage.selectReviews(count, filmId).stream()
                .map(reviewMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public void addLikeToReview(long reviewId, long userId) {

        if (!reviewStorage.existById(reviewId)) {
            throw new ReviewNotFoundException("Отзыв не найден");
        }

        if (!userStorage.existsById(userId)) {
            throw new UserNotFoundException("Пользователь не найден");
        }

        Optional<String> reactionOpt = reviewStorage.getReactionType(reviewId, userId);
        String reaction = reactionOpt.orElse(null);

        if ("LIKE".equals(reaction)) {
            throw new ReactionNotFound("Оценка отзыва reviewId = " + reviewId +
                    " от пользователя с id = " + userId + " не найдена");
        }

        if ("DISLIKE".equals(reaction)) {
            reviewStorage.deleteReaction(reviewId, userId);
            reviewStorage.incrementUseful(reviewId); // -1 → +1 = +2 переход
        } else {
            reviewStorage.incrementUseful(reviewId); // NULL → LIKE
        }

        reviewStorage.addLikeToReview(reviewId, userId);
    }

    @Override
    @Transactional
    public void addDislikeToReview(long reviewId, long userId) {

        if (!reviewStorage.existById(reviewId)) {
            throw new ReviewNotFoundException("Отзыв не найден");
        }

        if (!userStorage.existsById(userId)) {
            throw new UserNotFoundException("Пользователь не найден");
        }

        Optional<String> reactionOpt = reviewStorage.getReactionType(reviewId, userId);
        String reaction = reactionOpt.orElse(null);

        if ("DISLIKE".equals(reaction)) {
            throw new ReactionNotFound("Оценка отзыва reviewId = " + reviewId +
                    " от пользователя с id = " + userId + " не найдена");
        }

        if ("LIKE".equals(reaction)) {
            reviewStorage.deleteReaction(reviewId, userId);
            reviewStorage.decrementUseful(reviewId);
        } else {
            reviewStorage.decrementUseful(reviewId);
        }

        reviewStorage.addDislikeToReview(reviewId, userId);
    }

    @Override
    @Transactional
    public void deleteReaction(long reviewId, long userId) {

        if (!reviewStorage.existById(reviewId)) {
            throw new ReviewNotFoundException("Отзыв не найден");
        }

        if (!userStorage.existsById(userId)) {
            throw new UserNotFoundException("Пользователь не найден");
        }

        Optional<String> reactionOpt = reviewStorage.getReactionType(reviewId, userId);
        String reaction = reactionOpt.orElse(null);

        if (reaction == null) {
            throw new ReactionNotFound("Оценка отзыва reviewId = " + reviewId +
                    " от пользователя с id = " + userId + " не найдена");
        }

        if ("LIKE".equals(reaction)) {
            reviewStorage.decrementUseful(reviewId);
        } else if ("DISLIKE".equals(reaction)) {
            reviewStorage.incrementUseful(reviewId);
        }

        reviewStorage.deleteReaction(reviewId, userId);
    }
}
