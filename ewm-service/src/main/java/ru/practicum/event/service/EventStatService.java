//package ru.practicum.event.service;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.google.gson.Gson;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Component;
//import ru.practicum.StatisticClient;
//import ru.practicum.StatsDtoOutput;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class EventStatService {
//
//    private final StatisticClient statisticClient;
//    private final ObjectMapper objectMapper;
//    private final Gson gson;
//
//    public Map<Long, Long> getEventsViews(List<Long> events) {
//        List<StatsDtoOutput> stats;
//        Map<Long, Long> eventsViews = new HashMap<>();
//        List<String> uris = new ArrayList<>();
//
//        if (events == null || events.isEmpty()) {
//            return eventsViews;
//        }
//        for (Long id : events) {
//            uris.add("/events/" + id);
//        }
//        ResponseEntity<Object> response = statisticClient.getStatistics(LocalDateTime.now().minusDays(100).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
//                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), uris, true);
//        Object body = response.getBody();
//        if (body != null) {
//            String json = gson.toJson(body);
//            TypeReference<List<StatsDtoOutput>> typeRef = new TypeReference<>() {
//            };
//            try {
//                stats = objectMapper.readValue(json, typeRef);
//            } catch (JsonProcessingException e) {
//                throw new RuntimeException("Ошибка при загрузке данных из сервиса статистики");
//            }
//            for (Long event : events) {
//                eventsViews.put(event, 0L);
//            }
//            if (!stats.isEmpty()) {
//                for (StatsDtoOutput stat : stats) {
//                    eventsViews.put(Long.parseLong(stat.getUri().split("/", 0)[2]),
//                            stat.getHits());
//                }
//            }
//        }
//        return eventsViews;
//    }
//}
