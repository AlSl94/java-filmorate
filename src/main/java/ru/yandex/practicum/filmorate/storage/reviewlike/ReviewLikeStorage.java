package ru.yandex.practicum.filmorate.storage.reviewlike;

public interface ReviewLikeStorage {

    void putLike(Long reviewId, Long userId);

    void putDislike(Long reviewId, Long userId);

    void removeLike(Long reviewId, Long userId);

    void removeDislike(Long reviewId, Long userId);
}
