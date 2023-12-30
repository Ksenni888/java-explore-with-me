package ru.practicum.stats;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatsDtoOutput {
    private String app;
    private String uri;
    private long hits;
}
