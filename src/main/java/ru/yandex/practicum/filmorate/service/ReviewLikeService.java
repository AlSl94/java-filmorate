package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.reviewlike.ReviewLikeStorage;

@Service
public class ReviewLikeService {

    private final ReviewLikeStorage reviewLikeStorage;

    @Autowired
    public ReviewLikeService(ReviewLikeStorage reviewLikeStorage) {
        this.reviewLikeStorage = reviewLikeStorage;
    }

    /**
     * Метод, для лайка отзыва
     * @param reviewId айди отзыва
     * @param userId айди пользователя
     */
    public void putLike(Long reviewId, Long userId) {
        reviewLikeStorage.putLike(reviewId, userId);
    }

    /**
     * Метод для дизлайка отзыва
     * @param reviewId айди отзыва
     * @param userId айди пользователя
     */
    public void putDislike(Long reviewId, Long userId) {
        reviewLikeStorage.putDislike(reviewId, userId);
    }

    /**
     * Метод для удаления лайка у отзыва
     * @param reviewId айди отзыва
     * @param userId айди пользователя
     */
    public void removeLike(Long reviewId, Long userId) {
        reviewLikeStorage.removeLike(reviewId, userId);
    }

    /**
     * Метод для удаления дизлайка у отзыва
     * @param reviewId айди отзыва
     * @param userId айди пользователя
     */
    public void removeDislike(Long reviewId, Long userId) {
        reviewLikeStorage.removeDislike(reviewId, userId);
    }
}
