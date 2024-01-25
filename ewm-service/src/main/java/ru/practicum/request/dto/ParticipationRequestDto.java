package ru.practicum.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import static ru.practicum.constant.Constants.DATE_FORMAT;

@Getter
@Setter
public class ParticipationRequestDto {
    private long id;

    @JsonFormat(pattern = DATE_FORMAT)
    private LocalDateTime created;

    private long event;

    private long requester;

    private String status;
}