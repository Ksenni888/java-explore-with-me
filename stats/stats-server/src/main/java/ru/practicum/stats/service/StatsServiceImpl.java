package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.stats.StatsDto;
import ru.practicum.stats.StatsDtoOutput;
import ru.practicum.stats.mapper.StatsMapper;

import ru.practicum.stats.model.Stats;
import ru.practicum.stats.repository.StatsRepository;


import java.util.ArrayList;


@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;
    private final StatsMapper statsMapper;

    @Override
    public StatsDto saveStats(StatsDto statsDto) {
        Stats stats  = statsMapper.toStats(statsDto);
        statsRepository.save(stats);
        return statsDto;
    }

    @Override
    public StatsDtoOutput getStats(
            String start,
            String end,
            ArrayList<String> uris,
            boolean unique) {
        return null;
    }
}