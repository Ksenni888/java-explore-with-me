package ru.practicum.stats.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.practicum.stats.StatsDto;
import ru.practicum.stats.StatsDtoOutput;
import ru.practicum.stats.service.StatsService;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@RestController
@RequiredArgsConstructor
@Validated
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    public ResponseEntity<StatsDto> saveStats(@Valid @RequestBody StatsDto statsDto) {
        return new ResponseEntity<>(statsService.saveStats(statsDto), HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public StatsDtoOutput getStats(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam ArrayList<String> uris,
            @RequestParam boolean unique
    ) throws UnsupportedEncodingException {
        return statsService.getStats(encodeValue(start), encodeValue(end), uris, unique);
    }

    private String encodeValue(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    }
}
