package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import javax.validation.Valid;
import java.util.Collection;

@Service
@Validated
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final FilmService filmService;
    private final UserService userService;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage, FilmService filmService, UserService userService) {
        this.reviewStorage = reviewStorage;
        this.filmService = filmService;
        this.userService = userService;
    }

    public Review createReview(@Valid Review review) {
        checkReviewFilmAndUser(review);
        return reviewStorage.createReview(review);
    }

    public Review updateReview(@Valid Review review) {
        return reviewStorage.updateReview(review);
    }

    public void deleteReview(Long id) {
        reviewStorage.deleteReview(id);
    }

    public Review getReviewById(Long id) {
        return reviewStorage.getReviewById(id);
    }

    public Collection<Review> getMostUsefulReviews(Integer count, Long filmId) {
        return reviewStorage.getMostUsefulReviews(count, filmId);
    }

    private void checkReviewFilmAndUser(Review review) {
        filmService.findFilmById(review.getFilmId());
        userService.findUserById(review.getUserId());
    }
}
