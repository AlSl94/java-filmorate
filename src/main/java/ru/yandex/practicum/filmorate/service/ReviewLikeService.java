package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.reviewlike.ReviewLikeDbStorage;

@Service
public class ReviewLikeService {

    private final ReviewLikeDbStorage reviewLikeDbStorage;

    @Autowired
    public ReviewLikeService(ReviewLikeDbStorage reviewLikeDbStorage) {
        this.reviewLikeDbStorage = reviewLikeDbStorage;
    }

    public void putLike(Long reviewId, Long userId) {
        reviewLikeDbStorage.putLike(reviewId, userId);
    }

    public void putDislike(Long reviewId, Long userId) {
        reviewLikeDbStorage.putDislike(reviewId, userId);
    }

    public void removeLike(Long reviewId, Long userId) {
        reviewLikeDbStorage.removeLike(reviewId, userId);
    }

    public void removeDislike(Long reviewId, Long userId) {
        reviewLikeDbStorage.removeDislike(reviewId, userId);
    }
}
