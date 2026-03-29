package ru.yandex.practicum.filmorate.service.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dal.events.EventStorage;
import ru.yandex.practicum.filmorate.dal.film.FilmStorage;
import ru.yandex.practicum.filmorate.dal.review.ReviewStorage;
import ru.yandex.practicum.filmorate.dal.user.UserStorage;
import ru.yandex.practicum.filmorate.dto.review.request.ReviewRequestCreateDto;
import ru.yandex.practicum.filmorate.dto.review.request.ReviewRequestUpdateDto;
import ru.yandex.practicum.filmorate.dto.review.response.ReviewResponseDto;
import ru.yandex.practicum.filmorate.exception.DuplicateReviewException;
import ru.yandex.practicum.filmorate.exception.DuplicateReviewReactionException;
import ru.yandex.practicum.filmorate.exception.notFound.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.notFound.ReactionNotFound;
import ru.yandex.practicum.filmorate.exception.notFound.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.notFound.UserNotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final EventStorage eventStorage;

    @Override
    @Transactional
    public ReviewResponseDto createReview(ReviewRequestCreateDto dto) {
        if (!userStorage.existsById(dto.userId())) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        if (!filmStorage.existsById(dto.filmId())) {
            throw new FilmNotFoundException("Фильм не найден");
        }
        if (reviewStorage.existByIds(dto.userId(), dto.filmId())) {
            throw new DuplicateReviewException("Отзыв уже существует");
        }

        Review res = reviewStorage.save(ReviewMapper.toEntity(dto));

        eventStorage.addEvent(
                Event.of(res.getUserId(), Event.EventType.REVIEW, Event.Operation.ADD, res.getReviewId()
                )
        );

        log.info("save() – reviewId={}, userId={}, filmId={}, content={}, isPositive={}, useful={}",
                res.getReviewId(), res.getUserId(), res.getFilmId(),
                res.getContent(), res.getIsPositive(), res.getUseful());

        return ReviewMapper.toResponseDto(res);
    }

    @Override
    @Transactional
    public ReviewResponseDto updateReview(ReviewRequestUpdateDto dto) {
        if (!reviewStorage.existById(dto.reviewId())) {
            throw new ReviewNotFoundException("Отзыв не найден");
        }
        Review res = reviewStorage.update(ReviewMapper.toEntity(dto));

        eventStorage.addEvent(
                Event.of(
                        res.getUserId(),
                        Event.EventType.REVIEW,
                        Event.Operation.UPDATE,
                        res.getReviewId()
                )
        );

        return ReviewMapper.toResponseDto(res);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = reviewStorage.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review c id=" + reviewId + " не найден."));

        reviewStorage.deleteReview(reviewId);

        eventStorage.addEvent(
                Event.of(
                        review.getUserId(),
                        Event.EventType.REVIEW,
                        Event.Operation.REMOVE,
                        reviewId
                )
        );
    }

    @Override
    @Transactional
    public List<ReviewResponseDto> selectReviews(long count, Long filmId) {

        return reviewStorage.selectReviews(count, filmId).stream()
                .map(ReviewMapper::toResponseDto)
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

        Boolean isLike = reviewStorage.getReactionType(reviewId, userId);

        if (Boolean.TRUE.equals(isLike)) {
            throw new DuplicateReviewReactionException("Лайк отзыва reviewId = " + reviewId +
                                                       " от пользователя с id = " + userId + " уже стоит");
        }

        if (Boolean.FALSE.equals(isLike)) {
            reviewStorage.incrementUseful(reviewId);
            reviewStorage.deleteReaction(reviewId, userId);
        }

        reviewStorage.addLikeToReview(reviewId, userId);
        reviewStorage.incrementUseful(reviewId);
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

        Boolean isLike = reviewStorage.getReactionType(reviewId, userId);

        if (Boolean.FALSE.equals(isLike)) {
            throw new DuplicateReviewReactionException("Дизлайк отзыва reviewId = " + reviewId +
                                                       " от пользователя с id = " + userId + " уже стоит");
        }

        if (Boolean.TRUE.equals(isLike)) {
            reviewStorage.decrementUseful(reviewId);
            reviewStorage.deleteReaction(reviewId, userId);
        }

        reviewStorage.addDislikeToReview(reviewId, userId);
        reviewStorage.decrementUseful(reviewId);
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

        Boolean isLike = reviewStorage.getReactionType(reviewId, userId);

        if (isLike == null) {
            throw new ReactionNotFound("Оценка отзыва reviewId = " + reviewId +
                                       " от пользователя с id = " + userId + " не найдена");
        } else if (isLike) {
            reviewStorage.decrementUseful(reviewId);
        } else {
            reviewStorage.incrementUseful(reviewId);
        }

        reviewStorage.deleteReaction(reviewId, userId);
    }

    @Override
    @Transactional
    public ReviewResponseDto getReviewById(Long id) {
        Review review = reviewStorage.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Отзыв не найден"));
        return ReviewMapper.toResponseDto(review);
    }
}
