package ru.practicum.exeption;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static ru.practicum.constant.Constants.DATE_FORMAT;

@Getter
@Setter
@RequiredArgsConstructor
public class ErrorResponse {
    private final HttpStatus status;

    private final String reason;

    private final String message;

    @JsonFormat(pattern = DATE_FORMAT)
    private final LocalDateTime timestamp;
}