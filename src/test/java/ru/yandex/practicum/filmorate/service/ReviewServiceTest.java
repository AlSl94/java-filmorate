package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ReviewServiceTest {

    private final ReviewService reviewService;
    private final FilmService filmService;
    private final MpaService mpaService;
    private final DirectorService directorService;
    private final GenreService genreService;
    private final UserService userService;
    private final ReviewLikeService reviewLikeService;

    @Test
    void createReviewTest() {
        filmService.add(film());

        userService.create(users().get(0));
        userService.create(users().get(1));

        Review reviewOne = reviewService.createReview(reviews().get(0));

        assertThat(reviewOne.getReviewId()).isEqualTo(1);
        assertThat(reviewOne.getContent()).isEqualTo(reviews().get(0).getContent());
    }

    @Test
    void updateReviewTest() {
        filmService.add(film());

        userService.create(users().get(0));
        userService.create(users().get(1));

        Review reviewOne = reviewService.createReview(reviews().get(0));
        assertThat(reviewOne.getReviewId()).isEqualTo(1);
        assertThat(reviewOne.getContent()).isEqualTo(reviews().get(0).getContent());

        Review updatedReview = reviewService.updateReview(reviews().get(2));
        assertThat(updatedReview.getReviewId()).isEqualTo(1);
        assertThat(updatedReview.getContent()).isEqualTo(reviews().get(2).getContent());
    }

    @Test
    void deleteReviewTest() {
        filmService.add(film());

        userService.create(users().get(0));
        userService.create(users().get(1));

        reviewService.createReview(reviews().get(0));
        Review reviewTwo = reviewService.createReview(reviews().get(1));

        List<Review> reviews = (List<Review>) reviewService.getMostUsefulReviews(2, 1L);
        assertThat(reviews).hasSize(2)
                .contains(reviewTwo);

        reviewService.deleteReview(reviewTwo.getReviewId());
        List<Review> updatedReviews = (List<Review>) reviewService.getMostUsefulReviews(2, 1L);
        assertThat(updatedReviews).hasSize(1)
                .doesNotContain(reviewTwo);
    }

    @Test
    void getReviewByIdTest() {
        filmService.add(film());
        userService.create(users().get(0));

        Review reviewOne = reviewService.createReview(reviews().get(0));
        Review reviewById = reviewService.getReviewById(1L);

        assertThat(reviewById).isEqualTo(reviewOne);
    }

    @Test
    void getMostUsefulReviewsTest() {
        filmService.add(film());
        userService.create(users().get(0));
        userService.create(users().get(1));
        Review reviewOne = reviewService.createReview(reviews().get(0));
        Review reviewTwo = reviewService.createReview(reviews().get(1));

        reviewLikeService.putLike(1L, 1L);

        List<Review> reviews = (List<Review>) reviewService.getMostUsefulReviews(2, 1L);
        assertThat(reviews.get(0)).isEqualTo(reviewOne);

        reviewLikeService.putLike(2L, 1L);
        reviewLikeService.putLike(2L, 2L);

        List<Review> updatedReviews = (List<Review>) reviewService.getMostUsefulReviews(2, 1L);
        assertThat(updatedReviews.get(0)).isEqualTo(reviewTwo);
    }

    private List<Review> reviews() {
        List<Review> reviews = new ArrayList<>();
        reviews.add(new Review(null, "Отличный фильм", true, 1L,1L,0));
        reviews.add(new Review(null, "Плохой фильм", false, 2L,1L,0));
        reviews.add(new Review(1L, "Фильм понравился", true, 1L,2L,0));
        return reviews;
    }

    private List<User> users() {
        List<User> users = new ArrayList<>();
        users.add(new User(null, "testOne@mail.ru", "TestNameOne", "TestLoginOne",
                LocalDate.of(1994, 2, 10)));
        users.add(new User(null, "testTwo@mail.ru", "TestNameTwo", "TestLoginTwo",
                LocalDate.of(2000, 2, 22)));
        return users;
    }

    private Film film() {
        directorService.create(new Director(1, "Вася Спилберг"));
        return new Film(null, "Крепкий орешек", "Фильм о лысом парне",
                LocalDate.of(1988, 7, 12), mpaService.getMpaById(4), 2.13,
                Collections.singletonList(directorService.findDirectorById(1)),
                List.of(genreService.getGenreById(3), genreService.getGenreById(5)), 0.0);
    }
}