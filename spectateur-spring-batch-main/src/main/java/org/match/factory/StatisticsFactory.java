package org.match.factory;

import org.match.models.StatisticType;
import org.match.models.Statistics;

import java.time.LocalDateTime;

public class StatisticsFactory {
    // Méthode helper pour éviter duplication de code
    public static Statistics createStatistic(String key, StatisticType type,
                                       String description, String unit, Double value) {
        return Statistics.builder()
                .key(key)
                .calculatedAt(LocalDateTime.now())
                .statisticType(type)
                .description(description)
                .unit(unit)
                .value(value)
                .build();
    }

    // Surcharge avec matchId
    public static Statistics createStatistic(String key, StatisticType type,
                                       String description, String unit,
                                       Double value, String matchId) {
        return Statistics.builder()
                .key(key)
                .calculatedAt(LocalDateTime.now())
                .statisticType(type)
                .description(description)
                .unit(unit)
                .value(value)
                .matchId(matchId)
                .build();
    }

}
