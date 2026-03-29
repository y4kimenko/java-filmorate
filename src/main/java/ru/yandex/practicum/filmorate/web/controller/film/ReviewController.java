package ru.yandex.practicum.filmorate.web.controller.film;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.review.request.ReviewRequestCreateDto;
import ru.yandex.practicum.filmorate.dto.review.request.ReviewRequestUpdateDto;
import ru.yandex.practicum.filmorate.dto.review.response.ReviewResponseDto;
import ru.yandex.practicum.filmorate.service.review.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Validated
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponseDto createReview(@Valid
                                          @RequestBody ReviewRequestCreateDto review
    ) {
        return reviewService.createReview(review);
    }

    @PutMapping
    public ReviewResponseDto updateReview(@Valid
                                          @RequestBody
                                          ReviewRequestUpdateDto review) {
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable @PositiveOrZero(message = "id не может быть отрицательным") Long id) {
        reviewService.deleteReview(id);
    }

    @GetMapping
    public List<ReviewResponseDto> selectReviews(@RequestParam(defaultValue = "10")
                                                 @PositiveOrZero(message = "count  не может быть отрицательным")
                                                 Long count,

                                                 @RequestParam(required = false)
                                                 @PositiveOrZero
                                                 Long filmId

    ) {
        return reviewService.selectReviews(count, filmId);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeToReview(@PathVariable
                                @PositiveOrZero(message = "id не может быть отрицательным")
                                Long id,

                                @PathVariable
                                @PositiveOrZero(message = "userId не может быть отрицательным")
                                Long userId
    ) {
        reviewService.addLikeToReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDisLikeToReview(@PathVariable
                                   @PositiveOrZero(message = "id не может быть отрицательным")
                                   Long id,

                                   @PathVariable
                                   @PositiveOrZero(message = "userId не может быть отрицательным")
                                   Long userId
    ) {
        reviewService.addDislikeToReview(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteReaction(@PathVariable
                               @PositiveOrZero(message = "id не может быть отрицательным")
                               Long id,

                               @PathVariable
                               @PositiveOrZero(message = "userId не может быть отрицательным")
                               Long userId
    ) {
        reviewService.deleteReaction(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable
                              @PositiveOrZero(message = "id не может быть отрицательным")
                              Long id,

                              @PathVariable
                              @PositiveOrZero(message = "userId не может быть отрицательным")
                              Long userId
    ) {
        reviewService.deleteReaction(id, userId);
    }

    @GetMapping("/{id}")
    public ReviewResponseDto getReviewById(
            @PathVariable
            @PositiveOrZero(message = "id не может быть отрицательным")
            Long id) {
        return reviewService.getReviewById(id);
    }
}
