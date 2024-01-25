package ru.practicum.event.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.event.dto.EventAdminParam;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.EventUserParam;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    EventFullDto addEventPrivate(long userId, NewEventDto newEventDto);

    List<EventShortDto> getEventPrivate(long userId, Pageable pageable);

    EventFullDto getEventByIdPrivate(long userId, long eventId);

    EventFullDto updateEventPrivate(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<EventFullDto> getEventsAdmin(EventAdminParam eventAdminParam);

    EventFullDto updateEventAdmin(long eventId, UpdateEventAdminRequest updateEventAdmin);

    List<EventShortDto> getEventsPublic(EventUserParam eventUserParam, HttpServletRequest request);

    EventFullDto getEventByIdPublic(long id, HttpServletRequest request);

    List<ParticipationRequestDto> getRequestsUserToEventPrivate(long userId, long eventId);

    EventRequestStatusUpdateResult updateEventRequestStatusPrivate(long userId, long eventId, EventRequestStatusUpdateRequest updateRequests);
}