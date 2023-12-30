package ru.practicum.stats;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class StatsDto {
    private String app;
    private String uri;
    private String ip;
    private String timestamp;
}