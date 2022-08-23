package ru.yandex.practicum.filmorate.storage.reviewlike;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.WrongParameterException;

import java.util.Objects;

@Component
public class ReviewLikeDbStorage implements ReviewLikeStorage{

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewLikeDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void putLike(Long reviewId, Long userId) {
        jdbcTemplate.update("INSERT INTO REVIEW_LIKES (REVIEW_ID, USER_ID, IS_LIKE) VALUES (?, ?, 1)",
                reviewId, userId);
    }

    @Override
    public void putDislike(Long reviewId, Long userId) {
        jdbcTemplate.update("INSERT INTO REVIEW_LIKES (REVIEW_ID, USER_ID, IS_LIKE) VALUES (?, ?, -1)",
                reviewId, userId);
    }

    @Override
    public void removeLike(Long reviewId, Long userId) {
        checkLikeOrDislike(reviewId, userId, 1);
        jdbcTemplate.update("DELETE FROM REVIEW_LIKES WHERE REVIEW_ID = ? AND USER_ID = ?", reviewId, userId);
    }

    @Override
    public void removeDislike(Long reviewId, Long userId) {
        checkLikeOrDislike(reviewId, userId, -1);
        jdbcTemplate.update("DELETE FROM REVIEW_LIKES WHERE REVIEW_ID = ? AND USER_ID = ?", reviewId, userId);
    }

    private void checkLikeOrDislike(Long reviewId, Long userId, Integer isLike) {
        Byte countReviewLike = Objects.requireNonNull(jdbcTemplate.queryForObject("SELECT COUNT(REVIEW_ID) FROM REVIEW_LIKES " +
                        "WHERE REVIEW_ID = ? AND USER_ID = ? AND IS_LIKE = ?",
                Byte.class, reviewId, userId, isLike));
        if (countReviewLike == 0) {
            throw new WrongParameterException(String.format
                    ("Лайк/дизлайк отзыва с id: %d от пользователя с id %d не найден", reviewId, userId));
        }
    }
}
