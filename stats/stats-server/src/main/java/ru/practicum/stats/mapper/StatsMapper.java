package ru.practicum.stats.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.stats.StatsDto;
import ru.practicum.stats.model.Stats;


@Component
@RequiredArgsConstructor
public class StatsMapper {
    public Stats toStats(StatsDto statsDto) {
        Stats stats = new Stats();
        stats.setApp(statsDto.getApp());
        stats.setUri(statsDto.getUri());
        stats.setTimestamp(statsDto.getTimestamp());
        return stats;
    }
}