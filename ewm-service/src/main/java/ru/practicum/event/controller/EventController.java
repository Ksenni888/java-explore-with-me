package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.StatsClient;
import ru.practicum.event.dto.EventAdminParam;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.EventUserParam;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.constant.Constants.DATE_FORMAT;

@RestController
@RequiredArgsConstructor
@Validated
public class EventController {
    private final EventService eventService;
    private final StatsClient client;


    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEventPrivate(@PathVariable long userId, @Valid @RequestBody NewEventDto newEventDto) {
        return eventService.addEventPrivate(userId, newEventDto);
    }

    @GetMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEventPrivate(@PathVariable long userId,
                                               @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
                                               @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {
        return eventService.getEventPrivate(userId, PageRequest.of(from / size, size));
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventByIdPrivate(@PathVariable long userId,
                                            @PathVariable long eventId) {
        return eventService.getEventByIdPrivate(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventPrivate(@PathVariable long userId, @PathVariable long eventId,
                                           @Valid @RequestBody UpdateEventUserRequest updateEvent) {
        return eventService.updateEventPrivate(userId, eventId, updateEvent);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequestsUserToEventPrivate(@PathVariable long userId,
                                                                       @PathVariable long eventId) {
        return eventService.getRequestsUserToEventPrivate(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateEventRequestStatusPrivate(@PathVariable long userId,
                                                                          @PathVariable long eventId,
                                                                          @Valid @RequestBody EventRequestStatusUpdateRequest updateRequests) {
        return eventService.updateEventRequestStatusPrivate(userId, eventId, updateRequests);
    }

    @GetMapping("/admin/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getEventsAdmin(@RequestParam(required = false) List<Long> users,
                                             @RequestParam(required = false) List<String> states,
                                             @RequestParam(required = false) List<Long> categories,
                                             @RequestParam(required = false) @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime rangeStart,
                                             @RequestParam(required = false) @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime rangeEnd,
                                             @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
                                             @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {
        EventAdminParam eventAdminParam = new EventAdminParam();
        eventAdminParam.setUsers(users);
        eventAdminParam.setStates(states);
        eventAdminParam.setCategories(categories);
        eventAdminParam.setRangeStart(rangeStart);
        eventAdminParam.setRangeEnd(rangeEnd);
        eventAdminParam.setFrom(from);
        eventAdminParam.setSize(size);

        return eventService.getEventsAdmin(eventAdminParam);
    }

    @PatchMapping("/admin/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventAdmin(@PathVariable long eventId, @Valid @RequestBody UpdateEventAdminRequest updateEventAdmin) {
        return eventService.updateEventAdmin(eventId, updateEventAdmin);
    }

    @GetMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEventsPublic(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime rangeEnd,
            @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            HttpServletRequest request) {

        EventUserParam eventUserParam = new EventUserParam();
        eventUserParam.setText(text);
        eventUserParam.setCategories(categories);
        eventUserParam.setPaid(paid);
        eventUserParam.setRangeStart(rangeStart);
        eventUserParam.setRangeEnd(rangeEnd);
        eventUserParam.setOnlyAvailable(onlyAvailable);
        eventUserParam.setSort(sort);
        eventUserParam.setFrom(from);
        eventUserParam.setSize(size);

        return eventService.getEventsPublic(eventUserParam, request);
    }

    @GetMapping("/events/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventByIdPublic(@PathVariable long id, HttpServletRequest request) {
        return eventService.getEventByIdPublic(id, request);
    }
}