package ru.yandex.practicum.filmorate.storage.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.WrongParameterException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;

@Component
@Qualifier()
public class ReviewDbStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Review createReview(Review review) {
        checkReviewFilmAndUser(review);
        String sql = "INSERT INTO REVIEWS (CONTENT, IS_POSITIVE, USER_ID, FILM_ID) VALUES ( ?,?,?,? )";
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
        review.setReviewId(keyHolder.getKey().longValue());
        System.out.println(review);
        System.out.println(review.getReviewId().getClass());
        System.out.println(review.getReviewId());
        return review;
    }

    public Review updateReview(Review review) {
        checkReviewExists(review.getReviewId());
        jdbcTemplate.update("UPDATE REVIEWS SET CONTENT = ?, IS_POSITIVE = ? " +
                        "WHERE REVIEW_ID = ?"
                , review.getContent()
                , review.getIsPositive()
                , review.getReviewId());
        return review;
    }

    public void deleteReview(Long id) {
        checkReviewExists(id);
        jdbcTemplate.update("DELETE FROM REVIEWS WHERE REVIEW_ID = ?", id);
    }

    public Review getReviewById(Long id) {
        checkReviewExists(id);
        Review review = jdbcTemplate.queryForObject("SELECT * FROM REVIEWS WHERE REVIEW_ID = ?"
                , this::mapRowToReview, id);
        getReviewUseful(review);
        return review;
    }

    public Collection<Review> getMostUsefulReviews(Integer count, Long filmId) {
        Collection<Review> reviewCollection;
        if (filmId != null) {
            reviewCollection = jdbcTemplate.query("SELECT * FROM REVIEWS " +
                            "LEFT JOIN REVIEW_LIKES AS RL on REVIEWS.REVIEW_ID = RL.REVIEW_ID " +
                            "WHERE REVIEWS.FILM_ID = ? " +
                            "GROUP BY REVIEWS.REVIEW_ID " +
                            "ORDER BY IFNULL(SUM(RL.IS_LIKE), 0) DESC " +
                            "LIMIT ?"
                    , this::mapRowToReview, filmId, count);
        } else {
            reviewCollection = jdbcTemplate.query("SELECT * FROM REVIEWS " +
                            "LEFT JOIN REVIEW_LIKES AS RL on REVIEWS.REVIEW_ID = RL.REVIEW_ID " +
                            "GROUP BY REVIEWS.REVIEW_ID " +
                            "ORDER BY IFNULL(SUM(RL.IS_LIKE), 0) DESC " +
                            "LIMIT ?"
                    , this::mapRowToReview, count);
        }
        getReviewUseful(reviewCollection);
        return reviewCollection;
    }

    private Review mapRowToReview(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(rs.getLong("review_id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .userId(rs.getLong("user_id"))
                .filmId(rs.getLong("film_id"))
                .build();
    }

    private void checkReviewExists(Long reviewId) {
        Byte countReview = jdbcTemplate.queryForObject("SELECT COUNT(REVIEW_ID) FROM REVIEWS WHERE REVIEW_ID = ?",
                Byte.class, reviewId);
        if (countReview == 0) {
            throw new WrongParameterException(String.format("Отзыв с id %d не найден", reviewId));
        }
    }

    private void getReviewUseful(Review review) {
        Integer useful = jdbcTemplate.queryForObject(
                "SELECT SUM(IS_LIKE)" +
                        "FROM REVIEW_LIKES " +
                        "WHERE REVIEW_ID = ? ", Integer.class, review.getReviewId());
        System.out.println(useful + "useful из базы");
        review.setUseful(Objects.requireNonNullElse(useful, 0));
    }

    private void getReviewUseful(Collection<Review> reviews) {
        for (Review review : reviews) {
            getReviewUseful(review);
        }
    }

    private void checkReviewFilmAndUser(Review review) {
        Byte countFilm = jdbcTemplate.queryForObject("SELECT COUNT(FILM_ID) FROM FILMS WHERE FILM_ID = ?",
                Byte.class, review.getFilmId());
        if (countFilm == 0) {
            throw new WrongParameterException(String.format("Ошибка запроса, неверный id фильма: %d",
                    review.getFilmId()));
        }
        Byte countUser = jdbcTemplate.queryForObject("SELECT COUNT(USER_ID) FROM USERS WHERE USER_ID = ?",
                Byte.class, review.getUserId());
        if (countUser == 0) {
            throw new WrongParameterException(String.format("Ошибка запроса, неверный id пользователя: %d",
                    review.getUserId()));
        }
    }
}
