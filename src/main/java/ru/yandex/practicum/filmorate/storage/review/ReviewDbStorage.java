package ru.yandex.practicum.filmorate.storage.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.WrongParameterException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;

@Component
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;
    private final EventStorage eventStorage;

    @Autowired
    public ReviewDbStorage(JdbcTemplate jdbcTemplate, EventStorage eventStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.eventStorage = eventStorage;
    }

    @Override
    public Review createReview(Review review) {
        final String sql = "INSERT INTO REVIEWS (CONTENT, POSITIVE, USER_ID, FILM_ID) VALUES ( ?,?,?,? )";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(
                    sql,
                    new String[]{"REVIEW_ID"});
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setLong(3, review.getUserId());
            stmt.setLong(4, review.getFilmId());
            return stmt;
        }, keyHolder);
        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        eventStorage.createEvent(review.getUserId(), review.getReviewId(), 3, 1);
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        checkReviewExists(review.getReviewId());
        Review reviewFromDb = getReviewById(review.getReviewId());
        jdbcTemplate.update("UPDATE REVIEWS SET CONTENT = ?, POSITIVE = ? " +
                        "WHERE REVIEW_ID = ?"
                , review.getContent()
                , review.getIsPositive()
                , reviewFromDb.getReviewId());
        eventStorage.createEvent(reviewFromDb.getUserId(), reviewFromDb.getReviewId(), 3, 2);
        return review;
    }

    @Override
    public void deleteReview(Long id) {
        checkReviewExists(id);
        final Review review = getReviewById(id);
        jdbcTemplate.update("DELETE FROM REVIEWS WHERE REVIEW_ID = ?", id);
        eventStorage.createEvent(review.getUserId(), review.getReviewId(), 3, 3);
    }

    @Override
    public Review getReviewById(Long id) {
        checkReviewExists(id);
        final String sqlQuery = "SELECT REVIEWS.*, " +
                "IFNULL(SUM(RL.IS_LIKE=true), 0) - IFNULL(SUM(RL.IS_LIKE=false), 0) AS USEFUL FROM REVIEWS " +
                "LEFT JOIN REVIEW_LIKES AS RL on REVIEWS.REVIEW_ID = RL.REVIEW_ID " +
                "WHERE REVIEWS.REVIEW_ID = ? " +
                "GROUP BY REVIEWS.REVIEW_ID ";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToReview, id);
    }

    @Override
    public Collection<Review> getMostUsefulReviews(Integer limit, Long filmId) {
        Collection<Review> reviewCollection;
        if (filmId != null) {
            final String sqlQuery = "SELECT REVIEWS.*, " +
                    "IFNULL(SUM(RL.IS_LIKE=true), 0) - IFNULL(SUM(RL.IS_LIKE=false), 0) AS USEFUL FROM REVIEWS " +
                    "LEFT JOIN REVIEW_LIKES AS RL on REVIEWS.REVIEW_ID = RL.REVIEW_ID " +
                    "WHERE REVIEWS.FILM_ID = ? " +
                    "GROUP BY REVIEWS.REVIEW_ID " +
                    "ORDER BY IFNULL(SUM(RL.IS_LIKE=true), 0) - IFNULL(SUM(RL.IS_LIKE=false), 0) DESC " +
                    "LIMIT ?";
            reviewCollection = jdbcTemplate.query(sqlQuery, this::mapRowToReview, filmId, limit);
        } else {
            final String sqlQuery = "SELECT REVIEWS.*, " +
                    "IFNULL(SUM(RL.IS_LIKE=true), 0) - IFNULL(SUM(RL.IS_LIKE=false), 0) AS USEFUL FROM REVIEWS " +
                    "LEFT JOIN REVIEW_LIKES AS RL on REVIEWS.REVIEW_ID = RL.REVIEW_ID " +
                    "GROUP BY REVIEWS.REVIEW_ID " +
                    "ORDER BY IFNULL(SUM(RL.IS_LIKE=true), 0) - IFNULL(SUM(RL.IS_LIKE=false), 0) DESC " +
                    "LIMIT ?";
            reviewCollection = jdbcTemplate.query(sqlQuery, this::mapRowToReview, limit);
        }
        return reviewCollection;
    }

    private void checkReviewExists(Long reviewId) {
        Byte countReview = Objects.requireNonNull(jdbcTemplate.queryForObject(
                "SELECT COUNT(REVIEW_ID) FROM REVIEWS WHERE REVIEW_ID = ?",
                Byte.class, reviewId));
        if (countReview == 0) {
            throw new WrongParameterException(String.format("Отзыв с id %d не найден", reviewId));
        }
    }

    private Review mapRowToReview(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(rs.getLong("review_id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("positive"))
                .userId(rs.getLong("user_id"))
                .filmId(rs.getLong("film_id"))
                .useful(rs.getInt("useful"))
                .build();
    }
}