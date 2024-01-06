package ru.practicum.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.StatsDto;
import ru.practicum.model.Stats;

@Component
@RequiredArgsConstructor
public class StatsMapper {
    public Stats toStats(StatsDto statsDto) {
        Stats stats = new Stats();
        stats.setApp(statsDto.getApp());
        stats.setUri(statsDto.getUri());
        stats.setIp(statsDto.getIp());
        stats.setTimestamp(statsDto.getTimestamp());
        return stats;
    }
}