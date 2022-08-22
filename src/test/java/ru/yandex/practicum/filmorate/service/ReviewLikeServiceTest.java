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
class ReviewLikeServiceTest {

    private final ReviewLikeService reviewLikeService;
    private final ReviewService reviewService;
    private final FilmService filmService;
    private final MpaService mpaService;
    private final DirectorService directorService;
    private final GenreService genreService;
    private final UserService userService;

    @Test
    void putLike() {
        filmService.add(films().get(0));
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

    @Test
    void putDislike() {
        filmService.add(films().get(0));
        userService.create(users().get(0));
        userService.create(users().get(1));

        reviewService.createReview(reviews().get(0));
        Review reviewTwo = reviewService.createReview(reviews().get(1));

        reviewLikeService.putLike(1L, 1L);
        reviewLikeService.putLike(2L, 1L);
        reviewLikeService.putDislike(1L, 2L);

        List<Review> reviews = (List<Review>) reviewService.getMostUsefulReviews(2, 1L);
        assertThat(reviews.get(0)).isEqualTo(reviewTwo);
    }

    @Test
    void removeLike() {
        filmService.add(films().get(0));
        userService.create(users().get(0));
        userService.create(users().get(1));
        userService.create(users().get(2));

        Review reviewOne = reviewService.createReview(reviews().get(0));
        Review reviewTwo = reviewService.createReview(reviews().get(1));

        reviewLikeService.putLike(1L, 1L);
        reviewLikeService.putLike(1L, 2L);
        reviewLikeService.putLike(1L, 3L);
        reviewLikeService.putLike(2L, 1L);
        reviewLikeService.putLike(2L, 2L);

        List<Review> reviews = (List<Review>) reviewService.getMostUsefulReviews(2, 1L);
        assertThat(reviews.get(0)).isEqualTo(reviewOne);

        reviewLikeService.removeLike(1L, 1L);
        reviewLikeService.removeLike(1L, 2L);

        List<Review> updatedReviews = (List<Review>) reviewService.getMostUsefulReviews(2, 1L);
        assertThat(updatedReviews.get(0)).isEqualTo(reviewTwo);
    }

    @Test
    void removeDislike() {
        filmService.add(films().get(0));
        userService.create(users().get(0));
        userService.create(users().get(1));
        userService.create(users().get(2));
        userService.create(users().get(3));

        Review reviewOne = reviewService.createReview(reviews().get(0));
        Review reviewTwo = reviewService.createReview(reviews().get(1));

        reviewLikeService.putLike(1L, 1L);
        reviewLikeService.putLike(1L, 2L);
        reviewLikeService.putLike(2L, 1L);
        reviewLikeService.putDislike(1L, 3L);
        reviewLikeService.putDislike(1L, 4L);

        List<Review> reviews = (List<Review>) reviewService.getMostUsefulReviews(2, 1L);
        assertThat(reviews.get(0)).isEqualTo(reviewTwo);

        reviewLikeService.removeDislike(1L, 3L);
        reviewLikeService.removeDislike(1L, 4L);

        List<Review> updatedReviews = (List<Review>) reviewService.getMostUsefulReviews(2, 1L);
        assertThat(updatedReviews.get(0)).isEqualTo(reviewOne);
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
        users.add(new User(null, "testThree@mail.ru", "TestNameThree", "TestLoginThree",
                LocalDate.of(1950, 2, 22)));
        users.add(new User(null, "testFour@mail.ru", "TestNameFour",
                "TestLoginFour", LocalDate.of(1950, 1, 10)));
        return users;
    }

    private List<Film> films() {
        List<Film> films = new ArrayList<>();
        directorService.create(new Director(1, "Вася Спилберг"));
        films.add(new Film(null, "Крепкий орешек", "Фильм о лысом парне",
                LocalDate.of(1988, 7, 12), mpaService.getMpaById(4), 2.13,
                Collections.singletonList(directorService.findDirectorById(1)),
                List.of(genreService.getGenreById(3), genreService.getGenreById(5))));
        films.add(new Film(null, "Крепкий орешек 2", "Фильм о лысом парне 2",
                LocalDate.of(1990, 7, 2), mpaService.getMpaById(5), 2.04,
                Collections.singletonList(directorService.findDirectorById(1)),
                List.of(genreService.getGenreById(3), genreService.getGenreById(5))));
        return films;
    }
}