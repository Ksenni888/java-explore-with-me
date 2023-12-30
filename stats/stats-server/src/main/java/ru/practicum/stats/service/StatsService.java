package ru.practicum.stats.service;

import ru.practicum.stats.StatsDto;
import ru.practicum.stats.StatsDtoOutput;
import java.util.ArrayList;

public interface StatsService {
    StatsDto saveStats(StatsDto statsDto);

    StatsDtoOutput getStats(String start, String end, ArrayList<String> uris, boolean unique);
}
