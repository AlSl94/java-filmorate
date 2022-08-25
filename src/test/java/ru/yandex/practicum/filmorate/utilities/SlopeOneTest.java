package ru.yandex.practicum.filmorate.utilities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.utilites.SlopeOne;

import java.util.*;

class SlopeOneTest {

    @Test
    void getRecommendationTest_standardDeviation() {
        Map<Long, Double> targetUserRates = initializeTargetUserData();
        Map<Long, Map<Long, Double>> similarUsersFilmsRates = initializeSimilarUsersData();

        List<Long> expected = List.of(4L, 5L, 6L);
        List<Long> calculated = new SlopeOne<>(similarUsersFilmsRates, targetUserRates).getRecommendations();
        Assertions.assertEquals(expected, calculated);
    }

    @Test
    void getRecommendationTest_targetUserDontLikeAnyFilm() {
        Map<Long, Double> targetUserRates = new HashMap<>();
        Map<Long, Map<Long, Double>> similarUsersFilmsRates = initializeSimilarUsersData();

        List<Long> expected = List.of(1L, 2L, 3L, 4L, 5L, 6L);
        List<Long> calculated = new SlopeOne<>(similarUsersFilmsRates, targetUserRates).getRecommendations();
        Assertions.assertEquals(expected, calculated);
    }

    @Test
    void getRecommendationTest_targetUserHasNoIntersections() {
        Map<Long, Double> targetUserRates = initializeTargetUserData();
        Map<Long, Map<Long, Double>> similarUsersFilmsRates = new HashMap<>();
        similarUsersFilmsRates.put(1L, new HashMap<>());
        similarUsersFilmsRates.put(2L, new HashMap<>());
        similarUsersFilmsRates.put(3L, new HashMap<>());

        List<Long> expected = Collections.emptyList();
        List<Long> calculated = new SlopeOne<>(similarUsersFilmsRates, targetUserRates).getRecommendations();
        Assertions.assertEquals(expected, calculated);
    }

    private Map<Long, Double> initializeTargetUserData() {
        Map<Long, Double> targetUserRates = new HashMap<>();
        targetUserRates.put(1L, 1.0);
        targetUserRates.put(2L, 1.0);
        targetUserRates.put(3L, 1.0);
        targetUserRates.put(7L, 1.0);
        return targetUserRates;
    }

    private Map<Long, Map<Long, Double>> initializeSimilarUsersData() {
        Map<Long, Map<Long, Double>> similarUsersFilmsRates = new HashMap<>();

        Map<Long, Double> firstSimilarUserRates = new HashMap<>();
        firstSimilarUserRates.put(1L, 1.0);
        firstSimilarUserRates.put(2L, 1.0);
        firstSimilarUserRates.put(4L, 8.0);

        Map<Long, Double> secondSimilarUserRates = new HashMap<>();
        secondSimilarUserRates.put(2L, 1.0);
        secondSimilarUserRates.put(3L, 1.0);
        secondSimilarUserRates.put(5L, 1.0);

        Map<Long, Double> thirdSimilarUserRates = new HashMap<>();
        thirdSimilarUserRates.put(1L, 1.0);
        thirdSimilarUserRates.put(2L, 1.0);
        thirdSimilarUserRates.put(3L, 1.0);
        thirdSimilarUserRates.put(6L, 1.0);

        similarUsersFilmsRates.put(1L, firstSimilarUserRates);
        similarUsersFilmsRates.put(2L, secondSimilarUserRates);
        similarUsersFilmsRates.put(3L, thirdSimilarUserRates);

        return similarUsersFilmsRates;
    }
}
