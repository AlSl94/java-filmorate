package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewLikeService;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(value = "/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewLikeService reviewLikeService;

    @Autowired
    public ReviewController(ReviewService reviewService, ReviewLikeService reviewLikeService) {
        this.reviewService = reviewService;
        this.reviewLikeService = reviewLikeService;
    }

    @PostMapping
    public Review createReview(@RequestBody Review review) {
        reviewService.createReview(review);
        log.info("Добавлен отзыв: {}", review);
        return review;
    }

    @PutMapping
    public Review updateReview(@RequestBody Review review) {
        reviewService.updateReview(review);
        log.info("Обновлен отзыв: {}", review);
        return review;
    }

    @DeleteMapping(value = "/{id}")
    public void deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        log.info("Удален отзыв с id: {}", id);
    }

    @GetMapping(value = "/{id}")
    public Review getReview(@PathVariable Long id) {
        Review review = reviewService.getReviewById(id);
        log.info("Получен отзыв с id: {}", id);
        return review;
    }

    @GetMapping
    public Collection<Review> getMostUsefulReviews(@RequestParam(required = false) Long filmId,
                                                   @RequestParam(defaultValue = "10", required = false) Integer count) {
        Collection<Review> reviewCollection = reviewService.getMostUsefulReviews(count, filmId);
        log.info("Получен список отзывов в количестве: {}, по фильму с id {}", count, filmId);
        return reviewCollection;
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public void putLike(@PathVariable Long id, @PathVariable Long userId) {
        reviewLikeService.putLike(id, userId);
        log.info("Поставлен лайк с id {} от пользователя с id {}", id, userId);
    }

    @PutMapping(value = "/{id}/dislike/{userId}")
    public void putDislike(@PathVariable Long id, @PathVariable Long userId) {
        reviewLikeService.putDislike(id, userId);
        log.info("Поставлен дизлайк с id {} от пользователя с id {}", id, userId);
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        reviewLikeService.deleteLike(id, userId);
        log.info("Удален лайк с id {} от пользователя с id {}", id, userId);
    }

    @DeleteMapping(value = "/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable Long id, @PathVariable Long userId) {
        reviewLikeService.deleteDislike(id, userId);
        log.info("Удален дизлайк с id {} от пользователя с id {}", id, userId);
    }
}
