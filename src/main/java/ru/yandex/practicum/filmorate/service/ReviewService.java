package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewDbStorage;

import javax.validation.Valid;
import java.util.Collection;

@Service
@Validated
public class ReviewService {

    private final ReviewDbStorage reviewDbStorage;

    @Autowired
    public ReviewService(ReviewDbStorage reviewDbStorage) {
        this.reviewDbStorage = reviewDbStorage;
    }

    public Review createReview(@Valid Review review) {
        return reviewDbStorage.createReview(review);
    }

    public Review updateReview(@Valid Review review) {
        return reviewDbStorage.updateReview(review);
    }

    public void deleteReview(Long id) {
        reviewDbStorage.deleteReview(id);
    }

    public Review getReviewById(Long id) {
        return reviewDbStorage.getReviewById(id);
    }

    public Collection<Review> getMostUsefulReviews(Integer count, Long filmId) {
        return reviewDbStorage.getMostUsefulReviews(count, filmId);
    }
}
