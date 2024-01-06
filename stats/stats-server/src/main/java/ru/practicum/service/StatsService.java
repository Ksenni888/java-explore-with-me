package ru.practicum.service;

import ru.practicum.StatsDto;
import ru.practicum.StatsDtoOutput;

import java.util.List;

public interface StatsService {
    StatsDto saveStats(StatsDto statsDto);

    List<StatsDtoOutput> getStats(String start, String end, List<String> uris, Boolean unique);
}