package ru.yandex.practicum.filmorate.utilities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.utilites.SlopeOne;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class FilmRecommendationTest {

    @Test
    void getRecommendationTest_standardDeviation() {
        Map<Long, Integer> targetUserRates = initializeTargetUserData();
        Map<Long, Map<Long, Integer>> similarUsersFilmsRates = initializeSimilarUsersData();

        List<Long> expected = List.of(4L, 5L, 6L);
        List<Long> calculated = new SlopeOne<>(targetUserRates, similarUsersFilmsRates).getRecommendations();
        Assertions.assertEquals(expected, calculated);
    }

    @Test
    void getRecommendationTest_targetUserDontLikeAnyFilm() {
        Map<Long, Integer> targetUserRates = new HashMap<>();
        Map<Long, Map<Long, Integer>> similarUsersFilmsRates = initializeSimilarUsersData();

        List<Long> expected = List.of(1L, 2L, 3L, 4L, 5L, 6L);
        List<Long> calculated = new SlopeOne<>(targetUserRates, similarUsersFilmsRates).getRecommendations();
        Assertions.assertEquals(expected, calculated);
    }

    @Test
    void getRecommendationTest_targetUserHasNoIntersections() {
        Map<Long, Integer> targetUserRates = initializeTargetUserData();
        Map<Long, Map<Long, Integer>> similarUsersFilmsRates = new HashMap<>();
        similarUsersFilmsRates.put(1L, new HashMap<>());
        similarUsersFilmsRates.put(2L, new HashMap<>());
        similarUsersFilmsRates.put(3L, new HashMap<>());

        List<Long> expected = Collections.emptyList();
        List<Long> calculated = new SlopeOne<>(targetUserRates, similarUsersFilmsRates).getRecommendations();
        Assertions.assertEquals(expected, calculated);
    }

    private Map<Long, Integer> initializeTargetUserData() {
        Map<Long, Integer> targetUserRates = new HashMap<>();
        targetUserRates.put(1L, 1);
        targetUserRates.put(2L, 1);
        targetUserRates.put(3L, 1);
        targetUserRates.put(7L, 1);
        return targetUserRates;
    }

    private Map<Long, Map<Long, Integer>> initializeSimilarUsersData() {
        Map<Long, Map<Long, Integer>> similarUsersFilmsRates = new HashMap<>();

        Map<Long, Integer> firstSimilarUserRates = new HashMap<>();
        firstSimilarUserRates.put(1L, 1);
        firstSimilarUserRates.put(2L, 1);
        firstSimilarUserRates.put(4L, 8);

        Map<Long, Integer> secondSimilarUserRates = new HashMap<>();
        secondSimilarUserRates.put(2L, 1);
        secondSimilarUserRates.put(3L, 1);
        secondSimilarUserRates.put(5L, 1);

        Map<Long, Integer> thirdSimilarUserRates = new HashMap<>();
        thirdSimilarUserRates.put(1L, 1);
        thirdSimilarUserRates.put(2L, 1);
        thirdSimilarUserRates.put(3L, 1);
        thirdSimilarUserRates.put(6L, 1);

        similarUsersFilmsRates.put(1L, firstSimilarUserRates);
        similarUsersFilmsRates.put(2L, secondSimilarUserRates);
        similarUsersFilmsRates.put(3L, thirdSimilarUserRates);

        return similarUsersFilmsRates;
    }
}
