package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.StatsDtoOutput;
import ru.practicum.model.Stats;

import java.time.LocalDateTime;
import java.util.List;

    @Repository
    public interface StatsRepository extends JpaRepository<Stats, Long> {
        @Query("SELECT new ru.practicum.StatsDtoOutput(s.app, s.uri, COUNT (DISTINCT s.ip))" +
                "FROM Stats AS s " +
                "WHERE s.timestamp BETWEEN ?1 AND ?2 AND s.uri IN ?3 " +
                "GROUP BY s.app, s.uri " +
                "ORDER BY COUNT (s.ip) DESC")
        List<StatsDtoOutput> findAllStatsByTimeAndListOfUrisAndUniqueIp(
                LocalDateTime start, LocalDateTime end, List<String> uris);

        @Query("SELECT new ru.practicum.StatsDtoOutput(s.app, s.uri, COUNT (s.ip))" +
                "FROM Stats AS s " +
                "WHERE s.timestamp BETWEEN ?1 AND ?2 AND s.uri IN ?3 " +
                "GROUP BY s.app, s.uri " +
                "ORDER BY COUNT (s.ip) DESC")
        List<StatsDtoOutput> findAllStatsByTimeAndListOfUris(LocalDateTime start, LocalDateTime end, List<String> uris);

        @Query("SELECT new ru.practicum.StatsDtoOutput(s.app, s.uri, COUNT (DISTINCT s.ip))" +
                "FROM Stats AS s " +
                "WHERE s.timestamp BETWEEN ?1 AND ?2 " +
                "GROUP BY s.app, s.uri " +
                "ORDER BY COUNT (s.ip) DESC")
        List<StatsDtoOutput> findAllStatsByTimeAndUniqueIp(LocalDateTime start, LocalDateTime end);

        @Query("SELECT new ru.practicum.StatsDtoOutput(s.app, s.uri, COUNT (s.ip))" +
                "FROM Stats AS s " +
                "WHERE s.timestamp BETWEEN ?1 AND ?2 " +
                "GROUP BY s.app, s.uri " +
                "ORDER BY COUNT (s.ip) DESC")
        List<StatsDtoOutput> findAllStatsByTime(LocalDateTime start, LocalDateTime end);
    }