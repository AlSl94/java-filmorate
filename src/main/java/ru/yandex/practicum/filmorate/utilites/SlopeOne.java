package ru.yandex.practicum.filmorate.utilites;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @param <U> User (Пользователь, или сущность, выставляющая оценку Предмету)
 * @param <I> Item (Предмет, или сущность, которая может иметь оценку Пользователя)
 * @param <T>> Number type (Число, наследующее Number, являющееся оценкой Пользователя)
 */
@Slf4j
public class SlopeOne<U, I, T extends Number> {
    private final Map<I, Map<I, Double>> diffMatrix = new HashMap<>();
    private final Map<I, Map<I, Integer>> freqMatrix = new HashMap<>();
    private final Map<I, Double> userPredictMap = new HashMap<>();
    private final Map<I, Integer> userFrequencyMap = new HashMap<>();
    private final Map<I, T> targetUserRates;
    private final Map<U, Map<I, T>> commonRatesMap = new HashMap<>();
    private List<I> recommendedItemsList = new ArrayList<>();
    private final List<I> allItems = new ArrayList<>();

    /**
     * @param similarUserRatesMap - карта пользователей (без целевого) "с похожими интересами"
     * @param targetUserRates - оценки некоторого целевого пользователя
     */
    public SlopeOne(Map<U, Map<I, T>> similarUserRatesMap, Map<I, T> targetUserRates) {
        this.targetUserRates = targetUserRates;

        Map<U, Map<I, T>> targetUserRatesMap = new HashMap<>();
        targetUserRatesMap.put(null, new HashMap<>(targetUserRates));

        commonRatesMap.putAll(similarUserRatesMap);
        commonRatesMap.putAll(targetUserRatesMap);

        for (Map.Entry<U, Map<I, T>> entry : commonRatesMap.entrySet()) {
            for (Map.Entry<I, T> subEntry : entry.getValue().entrySet()) {
                if (!allItems.contains(subEntry.getKey())) {
                    allItems.add(subEntry.getKey());
                }
            }
        }
    }

    /**
     * @return список рекомендуемых Предметов для целевого пользователя, с учётом алгоритма Slope One
     */
    public List<I> getRecommendations() {
        buildMatrices();
        predictForTargetUser();
        return recommendedItemsList;
    }

    private void buildMatrices() {
        for (Map<I, ? extends Number> ratingMap : commonRatesMap.values()) {
            for (Map.Entry<I, ? extends Number> entryFirst : ratingMap.entrySet()) {
                I firstItem = entryFirst.getKey();
                double ratingFirst = entryFirst.getValue().doubleValue();

                if (!diffMatrix.containsKey(firstItem)) {
                    putEmptyMaps(firstItem);
                }

                calculateRatings(ratingMap, firstItem, ratingFirst);
            }
        }
        checkMatrices();
    }

    private void putEmptyMaps(I item) {
        diffMatrix.put(item, new HashMap<>());
        freqMatrix.put(item, new HashMap<>());
    }

    private void calculateRatings(Map<I, ? extends Number> ratingMap, I firstItem, double ratingFirst) {
        for (Map.Entry<I, ? extends Number> entryNext : ratingMap.entrySet()) {
            I nextItem = entryNext.getKey();
            double ratingNext = entryNext.getValue().doubleValue();

            int oldCount = 0;

            if (freqMatrix.get(firstItem).containsKey(nextItem)) {
                oldCount = freqMatrix.get(firstItem).get(nextItem);
            }

            double oldDiff = 0.0;

            if (diffMatrix.get(firstItem).containsKey(nextItem)) {
                oldDiff = diffMatrix.get(firstItem).get(nextItem);
            }

            double observedDiff = ratingFirst - ratingNext;
            freqMatrix.get(firstItem).put(nextItem, oldCount + 1);
            diffMatrix.get(firstItem).put(nextItem, oldDiff + observedDiff);
        }
    }

    private void checkMatrices() {
        for (Map.Entry<I, Map<I, Double>> entry : diffMatrix.entrySet()) {
            I item = entry.getKey();
            for (Map.Entry<I, Double> subEntry : entry.getValue().entrySet()) {
                I subItem = subEntry.getKey();
                double oldValue = diffMatrix.get(item).get(subItem);
                int count = freqMatrix.get(item).get(subItem);
                diffMatrix.get(item).put(subItem, oldValue / count);
            }
        }
    }

    private void predictForTargetUser() {

        for (I item : diffMatrix.keySet()) {
            userPredictMap.put(item, 0.0);
            userFrequencyMap.put(item, 0);
        }

        Set<I> itemSetFromRating = targetUserRates.keySet();

        for (I itemFromData : itemSetFromRating) {

            for (Map.Entry<I, Map<I, Double>> diffEntry : diffMatrix.entrySet()) {
                I itemFromDiff = diffEntry.getKey();
                try {
                    double filmDiffRating = diffMatrix.get(itemFromDiff).get(itemFromData);
                    double filmDataRating = targetUserRates.get(itemFromData).doubleValue();
                    int frequency = freqMatrix.get(itemFromDiff).get(itemFromData);
                    double predictedValue = filmDiffRating + filmDataRating;
                    double finalValue = predictedValue * frequency;
                    userPredictMap.put(itemFromDiff, userPredictMap.get(itemFromDiff) + finalValue);
                    userFrequencyMap.put(itemFromDiff, userFrequencyMap.get(itemFromDiff) + frequency);
                } catch (NullPointerException npe) {
                    log.error(npe.getMessage());
                }
            }
        }

        Map<I, Double> targetUserCleanMap = new HashMap<>();

        for (Map.Entry<I, Double> entry : userPredictMap.entrySet()) {
            I item = entry.getKey();
            if (userFrequencyMap.get(item) > 0) {
                targetUserCleanMap.put(item, userPredictMap.get(item) / userFrequencyMap.get(item));
            }
        }

        for (I item : allItems) {
            if (targetUserRates.containsKey(item)) {
                targetUserCleanMap.put(item, targetUserRates.get(item).doubleValue());
            } else targetUserCleanMap.putIfAbsent(item, -1.0);
        }

        recommendedItemsList = targetUserCleanMap.keySet().stream()
                .filter(item -> !targetUserRates.containsKey(item))
                .sorted(Comparator.comparing(targetUserCleanMap::get, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }
}