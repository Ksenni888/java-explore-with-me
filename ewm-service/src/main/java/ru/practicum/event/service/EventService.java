package ru.practicum.event.service;

import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;


import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto addEventPrivate(long userId, NewEventDto newEventDto);

    List<EventShortDto> getEventPrivate(long userId, Pageable pageable);

    EventFullDto getEventByIdPrivate(long userId, long eventId);

    EventFullDto updateEventPrivate(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest);

    public List<EventFullDto> getEventsAdmin(List<Long> users,
                                       List<String> states,
                                       List<Long> categories,
                                       LocalDateTime rangeStart,
                                       LocalDateTime rangeEnd,
                                       Pageable pageable);
}
