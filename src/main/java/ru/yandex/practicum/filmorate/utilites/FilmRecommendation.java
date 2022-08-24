package ru.yandex.practicum.filmorate.utilites;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class FilmRecommendation {
    public static final Double MARK_AT_WHICH_POSITIVE_RATING_STARTS = 6.0;

    public static List<Long> getRecommendation(Map<Long, Integer> targetUserFilmsRates, Map<Long,
            Map<Long, Integer>> similarUsersFilmsRates) {
        Set<Long> similarFilmsThatNotWatchedByTargetUSerIds = new HashSet<>();

        for (Long userId : similarUsersFilmsRates.keySet()) {
            Map<Long, Integer> userRates = similarUsersFilmsRates.get(userId);
            for (Long filmId : userRates.keySet()) {
                if (!targetUserFilmsRates.containsKey(filmId)) {
                    similarFilmsThatNotWatchedByTargetUSerIds.add(filmId);
                }
            }
        }

        Map<Long, Double> usersCorrelations = new HashMap<>();

        for (Long userId : similarUsersFilmsRates.keySet()) {
            usersCorrelations.put(userId, calculateCorrelation(targetUserFilmsRates,
                    similarUsersFilmsRates.get(userId)));
        }

        return getCorrelatedRecommendations(similarFilmsThatNotWatchedByTargetUSerIds, similarUsersFilmsRates, usersCorrelations);
    }

    private static Double calculateCorrelation(Map<Long, Integer> firstUserRates, Map<Long, Integer> secondUserRates) {
        int count = 0;
        double sum1 = 0.0;
        double sum2 = 0.0;
        double pSum = 0.0;
        double sqSum1 = 0.0;
        double sqSum2 = 0.0;

        for (Long filmId : firstUserRates.keySet()) {
            if (secondUserRates.containsKey(filmId)) {
                Integer firstUserRate = firstUserRates.get(filmId);
                Integer secondUserRate = secondUserRates.get(filmId);
                count++;
                sum1 += firstUserRate;
                sum2 += secondUserRate;
                pSum += firstUserRate * secondUserRate;
                sqSum1 += pow(firstUserRate, 2);
                sqSum2 += pow(secondUserRate, 2);
            }
        }

        double num = (pSum - (sum1 * sum2 / count));
        double den = sqrt((sqSum1 - (pow(sum1, 2)) / count) * (sqSum2 - (pow(sum2, 2)) / count));

        if (den == 0.0) {
            return 0.0;
        }
        return num / den;
    }

    private static List<Long> getCorrelatedRecommendations(Set<Long> filmsIds, Map<Long, Map<Long, Integer>> usersFilmRates,
                                                    Map<Long, Double> usersCorrelations) {
        Map<Long, Double> correlatedFilms = new HashMap<>();
        Map<Long, Double> sumCorrelations = new HashMap<>();

        for (Map.Entry<Long, Map<Long, Integer>> e : usersFilmRates.entrySet()) {
            Map<Long, Integer> userRates = e.getValue();
            Long userId = e.getKey();
            for (Long filmId : filmsIds) {
                if (userRates.containsKey(filmId)) {
                    Integer filmRate = userRates.get(filmId);
                    Double userCorrelation = usersCorrelations.get(userId);
                    double correlatedUserRate = filmRate * userCorrelation;
                    correlatedFilms.put(filmId, correlatedFilms.getOrDefault(filmId, 0.0) + correlatedUserRate);
                    sumCorrelations.put(filmId, sumCorrelations.getOrDefault(filmId, 0.0) + userCorrelation);
                }
            }
        }

        correlatedFilms.replaceAll((i, v) -> correlatedFilms.get(i) / sumCorrelations.get(i));

        return correlatedFilms.keySet().stream()
                .sorted(Comparator.comparingDouble(correlatedFilms::get).reversed())
                .collect(Collectors.toList());
    }
}
