package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.event.model.Location;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateEventUserRequest {
    @Size(min = 20, max = 2000)
    private String annotation;
   // @Positive
    private long category;
    @Size(min = 20, max = 7000)
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private String stateAction;
    @Size(min = 3, max = 120)
    private String title;
}