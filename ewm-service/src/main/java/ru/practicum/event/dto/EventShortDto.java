package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.user.dto.UserShortDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static ru.practicum.constant.Constants.DATE_FORMAT;

@Getter
@Setter
public class EventShortDto {
    private long id;

    @NotNull
    private String annotation;

    @NotNull
    private CategoryDto category;

    private Integer confirmedRequests;

    @NotNull
    @JsonFormat(pattern = DATE_FORMAT)
    private LocalDateTime eventDate;

    @NotNull
    private UserShortDto initiator;

    @NotNull
    private Boolean paid;

    @NotNull
    private String title;

    private Long views;
}