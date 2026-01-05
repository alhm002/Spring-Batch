package org.match.service;

import lombok.AllArgsConstructor;
import org.match.factory.StatisticsFactory;
import org.match.models.*;
import org.match.repository.EntrySpectateurRepository;
import org.match.repository.SpectateurRepository;
import org.match.repository.StatisticsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StatisticsService {

    private final StatisticsRepository statisticsRepository;
    private final SpectateurRepository spectateurRepository;
    private final EntrySpectateurRepository entrySpectateurRepository;

    @Transactional
    public void calculateAllStatistics() {
        long startTime = System.currentTimeMillis();

        try {
            // Supprimer les anciennes statistiques
            statisticsRepository.deleteAll();

            // Charger les données
            List<Spectateur> spectateurs = spectateurRepository.findAll();
            List<EntrySpectateur> entrySpectateurs = entrySpectateurRepository.findAll();

            // Validation des données
            if (spectateurs.isEmpty() && entrySpectateurs.isEmpty()) {
                return;
            }

            List<Statistics> allStats = new ArrayList<>();

            allStats.addAll(calculateNationalityStatistics(spectateurs));
            allStats.addAll(calculateTicketTypeStatistics(entrySpectateurs));
            allStats.addAll(calculateGateOccupancyStatistics(entrySpectateurs));
            allStats.addAll(calculateTribuneStatistics(entrySpectateurs));
            allStats.addAll(calculateBlocStatistics(entrySpectateurs));
            allStats.addAll(calculateMatchAttendanceStatistics(entrySpectateurs));
            allStats.addAll(calculateTop10ActiveSpectators(entrySpectateurs));
            allStats.addAll(calculateEntryTimeRangeStatistics(entrySpectateurs));

            statisticsRepository.saveAll(allStats);

        } catch (Exception e) {
            throw e;
        }
    }


    // Arrondir les pourcentages
    private double calculatePercentage(long count, int total) {
        if (total == 0) return 0.0;
        return Math.round((count * 100.0 / total) * 100.0) / 100.0;
    }

    private List<Statistics> calculateNationalityStatistics(List<Spectateur> spectateurs) {
        int totalSpectateurs = spectateurs.size();
        List<Statistics> allStatsNationality = new ArrayList<>();

        Map<String, Long> countByNationality = spectateurs.stream()
                .filter(s -> Objects.nonNull(s.getNationality()))
                .collect(Collectors.groupingBy(
                        Spectateur::getNationality,
                        Collectors.counting()
                ));

        for (Map.Entry<String, Long> entry : countByNationality.entrySet()) {
            String nationality = entry.getKey();
            Long count = entry.getValue();
            double percentage = calculatePercentage(count, totalSpectateurs);

            Statistics distribution = StatisticsFactory.createStatistic(
                    nationality,
                    StatisticType.NATIONALITY_DISTRIBUTION,
                    "Nombre de spectateurs de nationalité " + nationality,
                    "count",
                    count.doubleValue()
            );

            Statistics percentageStat = StatisticsFactory.createStatistic(
                    nationality,
                    StatisticType.NATIONALITY_PERCENTAGE,
                    "Pourcentage de spectateurs de nationalité " + nationality,
                    "%",
                    percentage
            );

            allStatsNationality.add(distribution);
            allStatsNationality.add(percentageStat);
        }

        return allStatsNationality;
    }

    private List<Statistics> calculateTicketTypeStatistics(List<EntrySpectateur> entrySpectateurs) {
        if (entrySpectateurs.isEmpty()) {
            return Collections.emptyList();
        }

        int totalEntries = entrySpectateurs.size();
        List<Statistics> allStatsTicketType = new ArrayList<>();

        Map<String, Long> countByTicketType = entrySpectateurs.stream()
                .filter(e -> Objects.nonNull(e.getTicketType()))
                .collect(Collectors.groupingBy(
                        e -> e.getTicketType().toString(),
                        Collectors.counting()
                ));

        for (Map.Entry<String, Long> entry : countByTicketType.entrySet()) {
            String ticketType = entry.getKey();
            Long count = entry.getValue();
            double percentage = calculatePercentage(count, totalEntries);

            Statistics distribution = StatisticsFactory.createStatistic(
                    ticketType,
                    StatisticType.TICKET_TYPE_DISTRIBUTION,
                    "Nombre de tickets de type " + ticketType,
                    "count",
                    count.doubleValue()
            );

            Statistics percentageStat = StatisticsFactory.createStatistic(
                    ticketType,
                    StatisticType.TICKET_TYPE_PERCENTAGE,
                    "Pourcentage de tickets de type " + ticketType,
                    "%",
                    percentage
            );

            allStatsTicketType.add(distribution);
            allStatsTicketType.add(percentageStat);
        }

        return allStatsTicketType;
    }

    private List<Statistics> calculateGateOccupancyStatistics(List<EntrySpectateur> entrySpectateurs) {
        if (entrySpectateurs.isEmpty()) {
            return Collections.emptyList();
        }

        int totalEntries = entrySpectateurs.size();
        List<Statistics> allStatsGate = new ArrayList<>();

        Map<String, Long> countByGate = entrySpectateurs.stream()
                .filter(e -> Objects.nonNull(e.getGate()))
                .collect(Collectors.groupingBy(
                        EntrySpectateur::getGate,
                        Collectors.counting()
                ));

        for (Map.Entry<String, Long> entry : countByGate.entrySet()) {
            String gate = entry.getKey();
            Long count = entry.getValue();
            double percentage = calculatePercentage(count, totalEntries);

            Statistics distribution = StatisticsFactory.createStatistic(
                    gate,
                    StatisticType.GATE_OCCUPANCY,
                    "Nombre d'entrées par " + gate,
                    "count",
                    count.doubleValue()
            );

            Statistics percentageStat = StatisticsFactory.createStatistic(
                    gate,
                    StatisticType.GATE_OCCUPANCY_PERCENTAGE,
                    "Pourcentage d'utilisation de " + gate,
                    "%",
                    percentage
            );

            allStatsGate.add(distribution);
            allStatsGate.add(percentageStat);
        }

        return allStatsGate;
    }

    private List<Statistics> calculateTribuneStatistics(List<EntrySpectateur> entrySpectateurs) {
        if (entrySpectateurs.isEmpty()) {
            return Collections.emptyList();
        }

        List<Statistics> allStatsTribune = new ArrayList<>();

        Map<String, Long> countByTribune = entrySpectateurs.stream()
                .filter(e -> Objects.nonNull(e.getSeatLocation()))
                .filter(e -> Objects.nonNull(e.getSeatLocation().getTribune()))
                .collect(Collectors.groupingBy(
                        e -> e.getSeatLocation().getTribune(),
                        Collectors.counting()
                ));

        for (Map.Entry<String, Long> entry : countByTribune.entrySet()) {
            String tribune = entry.getKey();
            Long count = entry.getValue();

            Statistics distribution = StatisticsFactory.createStatistic(
                    tribune,
                    StatisticType.TRIBUNE_DISTRIBUTION,
                    "Affluence tribune " + tribune,
                    "count",
                    count.doubleValue()
            );

            allStatsTribune.add(distribution);
        }

        String tribuneMostPopular = countByTribune.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Aucune tribune");

        Statistics mostPopular = StatisticsFactory.createStatistic(
                tribuneMostPopular,
                StatisticType.TRIBUNE_ATTENDANCE,
                "Tribune la plus populaire : " + tribuneMostPopular,
                "name",
                0.0
        );

        allStatsTribune.add(mostPopular);

        return allStatsTribune;
    }

    private List<Statistics> calculateBlocStatistics(List<EntrySpectateur> entrySpectateurs) {
        if (entrySpectateurs.isEmpty()) {
            return Collections.emptyList();
        }

        List<Statistics> allStatsBloc = new ArrayList<>();

        Map<String, Long> countByBloc = entrySpectateurs.stream()
                .filter(e -> Objects.nonNull(e.getSeatLocation()))
                .filter(e -> Objects.nonNull(e.getSeatLocation().getTribune()))
                .filter(e -> Objects.nonNull(e.getSeatLocation().getBloc()))
                .collect(Collectors.groupingBy(
                        e -> e.getSeatLocation().getTribune() + "-" + e.getSeatLocation().getBloc(),
                        Collectors.counting()
                ));

        for (Map.Entry<String, Long> entry : countByBloc.entrySet()) {
            String blocKey = entry.getKey();
            Long count = entry.getValue();

            Statistics blocStat = StatisticsFactory.createStatistic(
                    blocKey,
                    StatisticType.BLOC_ATTENDANCE,
                    "Affluence bloc " + blocKey,
                    "count",
                    count.doubleValue()
            );

            allStatsBloc.add(blocStat);
        }

        String mostPopularBloc = countByBloc.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Aucun bloc");

        Statistics mostPopular = StatisticsFactory.createStatistic(
                mostPopularBloc,
                StatisticType.BLOC_ATTENDANCE,
                "Bloc le plus populaire: " + mostPopularBloc,
                "name",
                0.0
        );

        allStatsBloc.add(mostPopular);

        return allStatsBloc;
    }

    private List<Statistics> calculateMatchAttendanceStatistics(List<EntrySpectateur> entrySpectateurs) {
        if (entrySpectateurs.isEmpty()) {
            return Collections.emptyList();
        }

        List<Statistics> allStatsMatch = new ArrayList<>();

        Map<String, Long> countByMatch = entrySpectateurs.stream()
                .filter(e -> Objects.nonNull(e.getMatchId()))
                .collect(Collectors.groupingBy(
                        EntrySpectateur::getMatchId,
                        Collectors.counting()
                ));

        for (Map.Entry<String, Long> entry : countByMatch.entrySet()) {
            String matchId = entry.getKey();
            Long count = entry.getValue();

            Statistics matchStat = StatisticsFactory.createStatistic(
                    matchId,
                    StatisticType.MATCH_ATTENDANCE,
                    "Nombre de spectateurs pour le match " + matchId,
                    "count",
                    count.doubleValue(),
                    matchId
            );

            allStatsMatch.add(matchStat);
        }

        return allStatsMatch;
    }

    private List<Statistics> calculateTop10ActiveSpectators(List<EntrySpectateur> entrySpectateurs) {
        if (entrySpectateurs.isEmpty()) {
            return Collections.emptyList();
        }

        List<Statistics> allStatsTop = new ArrayList<>();

        Map<String, Long> countBySpectator = entrySpectateurs.stream()
                .filter(e -> Objects.nonNull(e.getSpectateur()))
                .filter(e -> Objects.nonNull(e.getSpectateur().getSpectatorId()))
                .collect(Collectors.groupingBy(
                        e -> e.getSpectateur().getSpectatorId(),
                        Collectors.counting()
                ));

        List<Map.Entry<String, Long>> top10 = countBySpectator.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .toList();

        int rank = 1;
        for (Map.Entry<String, Long> entry : top10) {
            String spectatorId = entry.getKey();
            Long matchCount = entry.getValue();

            Statistics topStat = StatisticsFactory.createStatistic(
                    "RANK_" + rank + "_" + spectatorId,
                    StatisticType.TOP_ACTIVE_SPECTATORS,
                    "Rang " + rank + " : " + spectatorId + " avec " + matchCount + " matchs",
                    "matches",
                    matchCount.doubleValue()
            );

            allStatsTop.add(topStat);
            rank++;
        }

        return allStatsTop;
    }

    private List<Statistics> calculateEntryTimeRangeStatistics(List<EntrySpectateur> entrySpectateurs) {
        if (entrySpectateurs.isEmpty()) {
            return Collections.emptyList();
        }

        List<Statistics> allStatsTime = new ArrayList<>();

        Map<String, List<EntrySpectateur>> entriesByMatch = entrySpectateurs.stream()
                .filter(e -> Objects.nonNull(e.getMatchId()))
                .filter(e -> Objects.nonNull(e.getEntryTime()))
                .collect(Collectors.groupingBy(EntrySpectateur::getMatchId));

        for (Map.Entry<String, List<EntrySpectateur>> entry : entriesByMatch.entrySet()) {
            String matchId = entry.getKey();
            List<EntrySpectateur> entries = entry.getValue();

            Optional<LocalDateTime> minTime = entries.stream()
                    .map(EntrySpectateur::getEntryTime)
                    .min(LocalDateTime::compareTo);

            Optional<LocalDateTime> maxTime = entries.stream()
                    .map(EntrySpectateur::getEntryTime)
                    .max(LocalDateTime::compareTo);

            if (minTime.isPresent()) {
                LocalDateTime time = minTime.get();
                double minutesSinceMidnight = time.getHour() * 60.0 + time.getMinute();

                Statistics minStat = StatisticsFactory.createStatistic(
                        matchId + "_MIN",
                        StatisticType.ENTRY_TIME_MIN,
                        "Première entrée pour le match " + matchId + " : " + time,
                        "minutes",
                        minutesSinceMidnight,
                        matchId
                );

                allStatsTime.add(minStat);
            }

            if (maxTime.isPresent()) {
                LocalDateTime time = maxTime.get();
                double minutesSinceMidnight = time.getHour() * 60.0 + time.getMinute();

                Statistics maxStat = StatisticsFactory.createStatistic(
                        matchId + "_MAX",
                        StatisticType.ENTRY_TIME_MAX,
                        "Dernière entrée pour le match " + matchId + " : " + time,
                        "minutes",
                        minutesSinceMidnight,
                        matchId
                );

                allStatsTime.add(maxStat);
            }
        }
        return allStatsTime;
    }
}