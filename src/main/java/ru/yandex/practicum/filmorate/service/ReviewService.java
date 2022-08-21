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

    /**
     * Метод для создания отзыва
     * @param review отзыв
     * @return созданный отзыв
     */
    public Review createReview(@Valid Review review) {
        checkReviewFilmAndUser(review);
        return reviewStorage.createReview(review);
    }
    /**
     * Метод для обновления отзыва
     * @param review обновленный отзыв
     * @return обновленный отзыв
     */
    public Review updateReview(@Valid Review review) {
        return reviewStorage.updateReview(review);
    }

    /**
     * Метод для удаления отзыва
     * @param id айди отзыва
     */
    public void deleteReview(Long id) {
        reviewStorage.deleteReview(id);
    }

    /**
     * Метод для получения отзыва по айди
     * @param id айди отзыва
     * @return отзыв, полученный по айди
     */
    public Review getReviewById(Long id) {
        return reviewStorage.getReviewById(id);
    }

    /**
     * Метод для получения коллекция отсортированной по количеству лайков
     * @param count количество отзывов
     * @param filmId айди фильма, на который получаем отзывы
     * @return - коллекция с отзывами
     */
    public Collection<Review> getMostUsefulReviews(Integer count, Long filmId) {
        return reviewStorage.getMostUsefulReviews(count, filmId);
    }

    /**
     * Метод для проверки, что фильм и пользователь существуют
     * @param review отзыв
     */
    private void checkReviewFilmAndUser(Review review) {
        filmService.findFilmById(review.getFilmId());
        userService.findUserById(review.getUserId());
    }
}
