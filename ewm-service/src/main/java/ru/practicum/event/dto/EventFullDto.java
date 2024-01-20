package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.model.Location;
import ru.practicum.user.dto.UserShortDto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Getter
@Setter
public class EventFullDto {
    private long id;
    @NotNull
    private String annotation;
    @NotNull
    private CategoryDto category;
    private Integer confirmedRequests;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    private String description;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @NotNull
    private UserShortDto initiator;
    @NotNull
    private Location location;
    @NotNull
    private Boolean paid;
    @PositiveOrZero
    private Integer participantLimit;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;
    private Boolean requestModeration;
    private String state;
    @NotNull
    private String title;
    private Long views;
}