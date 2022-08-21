package ru.yandex.practicum.filmorate.utilites;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class FilmRecommendation {  // TODO тут предлагают добавить приватный конструктор
    public static List<Long> getRecommendation(Map<Long, Double> targetUserFilmsRates, Map<Long,
            Map<Long, Double>> similarUsersFilmsRates) {
        Set<Long> similarFilmsIds = new HashSet<>();  // TODO "содержимое этой коллекции обновляется, но не используется"
        Set<Long> similarFilmsThatNotWatchedByTargetUSerIds = new HashSet<>();

        for (Long userId : similarUsersFilmsRates.keySet()) {
            Map<Long, Double> userRates = similarUsersFilmsRates.get(userId);
            for (Long filmId : userRates.keySet()) {
                similarFilmsIds.add(filmId);
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

    private static Double calculateCorrelation(Map<Long, Double> firstUserRates, Map<Long, Double> secondUserRates) {
        int count = 0;
        double sum1 = 0.0;
        double sum2 = 0.0;
        double pSum = 0.0;
        double sqSum1 = 0.0;
        double sqSum2 = 0.0;

        for (Long filmId : firstUserRates.keySet()) {
            if (secondUserRates.containsKey(filmId)) {
                Double firstUserRate = firstUserRates.get(filmId);
                Double secondUserRate = secondUserRates.get(filmId);
                count++;
                sum1 += firstUserRate;
                sum2 += secondUserRate;
                pSum += firstUserRate * secondUserRate;
                sqSum1 += pow(firstUserRate, 2);
                sqSum2 += pow(secondUserRate, 2);
            }
        }

        double num = (pSum - (sum1 * sum2 / count));  // TODO предлагают проверить, что не делим на 0 (можем забить, если уверены)
        double den = sqrt((sqSum1 - (pow(sum1, 2)) / count) * (sqSum2 - (pow(sum2, 2)) / count));

        if (den == 0.0) {
            return 0.0;
        }
        return num / den;
    }

    private static List<Long> getCorrelatedRecommendations(Set<Long> filmsIds, Map<Long, Map<Long, Double>> usersFilmRates,
                                                    Map<Long, Double> usersCorrelations) {
        Map<Long, Double> correlatedFilms = new HashMap<>();
        Map<Long, Double> sumCorrelations = new HashMap<>();

        for (Map.Entry<Long, Map<Long, Double>> e : usersFilmRates.entrySet()) {
            Map<Long, Double> userRates = e.getValue();
            Long userId = e.getKey();
            for (Long filmId : filmsIds) {
                if (userRates.containsKey(filmId)) {
                    Double filmRate = userRates.get(filmId);
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
