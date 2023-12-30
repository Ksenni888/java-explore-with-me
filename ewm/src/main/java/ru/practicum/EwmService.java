package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.practicum.stats.StatsDto;
import ru.pructicum.stats.StatsClient;


import java.util.Random;

@RequiredArgsConstructor
@Service
public class EwmService {

    private final StatsClient client;

    public Integer getEvent() {
        StatsDto statsDto = new StatsDto();
        statsDto.setApp("ewm-main-service");
        statsDto.setIp("192.163.0.1");
        statsDto.setUri("/events/1");
        statsDto.setTimestamp("2022-09-06 11:00:23");

        Random random = new Random();
        client.saveStats(statsDto);
        return random.nextInt();
    }
}