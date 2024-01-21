package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class StatsClient {
    @Value("${stats.uri}")
    private String uri;
    private final RestTemplate restTemplate;

    public StatsDto saveStats(StatsDto statsDto) {
        ResponseEntity<StatsDto> response = restTemplate.postForEntity(uri + "/hit", statsDto, StatsDto.class);
        return response.getBody();
    }

    public List<StatsDtoOutput> getStats(String start, String end, List<String> uris, Boolean unique) {
        StringBuilder url = new StringBuilder();
        for (String uri : uris) {
            url.append("&uris=").append(uri);
        }

        String requestUri = uri + "/stats?start={start}&end={end}" + url + "&unique={unique}";
        Map<String, String> urlParameters = new HashMap<>();
        urlParameters.put("start", start);
        urlParameters.put("end", end);
        urlParameters.put("unique", Boolean.toString(unique));

        ResponseEntity<StatsDtoOutput[]> response = restTemplate.getForEntity(requestUri, StatsDtoOutput[].class, urlParameters);

        return response.getBody() != null ? Arrays.asList(response.getBody()) :
                Collections.emptyList();
    }
}