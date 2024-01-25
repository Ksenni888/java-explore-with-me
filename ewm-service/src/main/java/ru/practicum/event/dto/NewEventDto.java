package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.event.model.Location;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.constant.Constants.DATE_FORMAT;

@Getter
@Setter
public class NewEventDto {
    @NotNull
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotNull
    @Positive
    private long category;

    @NotNull
    @Size(min = 20, max = 7000)
    private String description;

    @NotNull
    @JsonFormat(pattern = DATE_FORMAT)
    private LocalDateTime eventDate;

    @NotNull
    private Location location;

    private Boolean paid;

    @PositiveOrZero
    private Integer participantLimit;

    private Boolean requestModeration;

    @NotNull
    @Size(min = 3, max = 120)
    private String title;
}