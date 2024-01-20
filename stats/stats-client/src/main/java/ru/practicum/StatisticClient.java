//package ru.practicum;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.web.client.RestTemplateBuilder;
//import org.springframework.http.ResponseEntity;
//import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
//import org.springframework.stereotype.Service;
//import org.springframework.web.util.DefaultUriBuilderFactory;
//
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class StatisticClient extends BaseClient {
//
//    @Autowired
//    public StatisticClient(@Value("${stats-server.url}") String serviceUrl, RestTemplateBuilder builder) {
//        super(
//                builder
//                        .uriTemplateHandler(new DefaultUriBuilderFactory(serviceUrl + ""))
//                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
//                        .build()
//        );
//    }
//
//    public ResponseEntity<Object> postHit(StatsDto inDto) {
//        return post("/hit", inDto);
//    }
//
//    public ResponseEntity<Object> getStatistics(String start, String end, List<String> uris, Boolean unique) {
//        StringBuilder url = new StringBuilder();
//        for (String uri : uris) {
//            url.append("&uris=").append(uri);
//        }
//        Map<String, Object> params = Map.of(
//                "start", start,
//                "end", end,
//                "unique", unique
//        );
//        return get("/stats?start={start}&end={end}" + url + "&unique={unique}", params);
//    }
//}
