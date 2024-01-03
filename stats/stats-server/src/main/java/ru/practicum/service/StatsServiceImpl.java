package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsDto;
import ru.practicum.StatsDtoOutput;
import ru.practicum.exeption.StatsValidationException;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.repository.StatsRepository;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {
    private static final Logger log = LoggerFactory.getLogger(StatsServiceImpl.class);
    private final StatsRepository statsRepository;
    private final StatsMapper statsMapper;

    @Override
    public StatsDto saveStats(StatsDto statsDto) {
        statsRepository.save(statsMapper.toStats(statsDto));
        log.info("Save new statistic information");
        return statsDto;
    }

    @Override
    public List<StatsDtoOutput> getStats(String start, String end, List<String> uris, Boolean unique) {
        log.info("Get statistic by parameters");
        LocalDateTime startTime = parseTime(decode(start));
        LocalDateTime endTime = parseTime(decode(end));
        List<StatsDtoOutput> statsDtoOutputs;

        if (startTime.isAfter(endTime)) {
            throw new StatsValidationException("Start's time must be before end");
        }

        if (uris != null) {
            if (unique) {
                statsDtoOutputs = statsRepository.findAllStatsByTimeAndListOfUrisAndUniqueIp(startTime, endTime, uris);
            } else {
                statsDtoOutputs = statsRepository.findAllStatsByTimeAndListOfUris(startTime, endTime, uris);
            }
        } else if (unique) {
            statsDtoOutputs = statsRepository.findAllStatsByTimeAndUniqueIp(startTime, endTime);
        } else {
            statsDtoOutputs = statsRepository.findAllStatsByTime(startTime, endTime);
        }
        return statsDtoOutputs;
    }

    private String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private LocalDateTime parseTime(String time) {
        try {
            return LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (DateTimeParseException e) {
            throw new StatsValidationException("Time's format must be: yyyy-MM-dd HH:mm:ss");
        }
    }
}