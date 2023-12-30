package ru.pructicum.stats;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.stats.StatsDto;

@RequiredArgsConstructor
@Service
public class StatsClient {
    @Value("${stats.uri}")
    private String uri;
    private final RestTemplate restTemplate;

//    public String saveStats(String item) {
//        ResponseEntity<String> response = restTemplate.postForEntity(uri + "/hit", item, String.class);
//        return response.getBody();
//    }

    public StatsDto saveStats(StatsDto statsDto) {
        ResponseEntity<StatsDto> response = restTemplate.postForEntity(uri + "/hit", statsDto, StatsDto.class);
        return response.getBody();
    }

    public Integer getStats() {
        ResponseEntity<Integer> response = restTemplate.getForEntity(uri + "/stats", Integer.class);
        return response.getBody();
    }

}

