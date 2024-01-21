package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.StatsDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventSort;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exeption.InvalidRequestException;
import ru.practicum.exeption.ObjectNotFoundException;
import ru.practicum.exeption.RulesViolationException;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private static final Logger log = LoggerFactory.getLogger(EventServiceImpl.class);
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final RequestMapper requestMapper;
    private final StatsClient client;

    @Override
    @Transactional
    public EventFullDto addEventPrivate(long userId, NewEventDto newEventDto) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new InvalidRequestException("Field: eventDate. " + "Error: Date must be after than now. Value:" + newEventDto.getEventDate());
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException(String.format("User with id=%d not found", userId)));
        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(() -> new ObjectNotFoundException(String.format("Category with id=%d not found", newEventDto.getCategory())));
        Event event = eventMapper.toEvent(newEventDto, category, user, null);
        event = eventRepository.save(event);

        log.info("Add new event");
        return eventMapper.toFull(event, 0L);
    }

    @Override
    public List<EventShortDto> getEventPrivate(long userId, Pageable pageable) {
        log.info("Get owner events");
        return eventRepository.findAllByInitiatorId(userId, pageable).stream()
                .map(x -> eventMapper.toShort(x, 0L))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventByIdPrivate(long userId, long eventId) {
//        Event event = eventRepository.findById(eventId).orElseThrow(
//                () -> new ObjectNotFoundException(String.format("Event with id=%d was not found", eventId)));
//        if (event.getInitiator().getId() != userId) {
//            throw new ObjectNotFoundException(String.format("Event with id=%d added another user", eventId));
//        }
        log.info("Get information about event for owner");
        return eventMapper.toFull(eventRepository.findByInitiatorIdAndId(userId, eventId), 0L);
    }

    @Override
    @Transactional
    public EventFullDto updateEventPrivate(long userId, long eventId, UpdateEventUserRequest updateEvent) {
        if (!eventRepository.existsById(eventId)) {
            throw new ObjectNotFoundException(String.format("Event with id=%d was not found", eventId));
        }
        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId);

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new RulesViolationException("Only pending or canceled events can be changed");
        }
        if (updateEvent.getEventDate() != null) {
            if (updateEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new InvalidRequestException("Field: eventDate. " + "Error: Date must be after than now. Value:" + event.getEventDate());
            } else {
                event.setEventDate(updateEvent.getEventDate());
            }
        }
        if (updateEvent.getCategory() != 0) {
            long categoryId = updateEvent.getCategory();
            Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ObjectNotFoundException(String.format("Category with id=%d was not found", categoryId)));
            event.setCategory(category);
        }
        if (updateEvent.getAnnotation() != null) {
            event.setAnnotation(updateEvent.getAnnotation());
        }
        if (updateEvent.getDescription() != null) {
            event.setDescription(updateEvent.getDescription());
        }
        if (updateEvent.getLocation() != null) {
            if (updateEvent.getLocation().getLat() != null) {
                event.setLat(updateEvent.getLocation().getLat());
            }
            if (updateEvent.getLocation().getLon() != null) {
                event.setLon(updateEvent.getLocation().getLon());
            }
        }
        if (updateEvent.getPaid() != null) {
            event.setPaid(updateEvent.getPaid());
        }
        if (updateEvent.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEvent.getParticipantLimit());
        }
        if (updateEvent.getRequestModeration() != null) {
            event.setRequestModeration(updateEvent.getRequestModeration());
        }
        if (updateEvent.getTitle() != null) {
            event.setTitle(updateEvent.getTitle());
        }
        if (updateEvent.getStateAction() != null) {
            if (updateEvent.getStateAction().equals("SEND_TO_REVIEW")) {
                event.setState(EventState.PENDING);
            } else if (updateEvent.getStateAction().equals("CANCEL_REVIEW")) {
                event.setState(EventState.CANCELED);
            } else {
                throw new InvalidRequestException("Unknown state, it must be: SEND_TO_REVIEW or CANCEL_REVIEW");
            }
        }
        log.info("Update Event");
        return eventMapper.toFull(eventRepository.save(event), 0L);
    }

    @Override
    public List<ParticipationRequestDto> getRequestsUserToEventPrivate(long userId, long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ObjectNotFoundException(String.format("Event with id=%d was not found", eventId));
        }
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException(String.format("User with id=%d not found", userId));
        }
        log.info("All requests to event");
        return requestRepository.findAllByEventId(eventId).stream()
                .map(x -> requestMapper.toDto(x))
                .collect(Collectors.toList());

    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateEventRequestStatusPrivate(long userId, long eventId,
                                                                          EventRequestStatusUpdateRequest updateRequest) {
        EventRequestStatusUpdateResult updateResult;
        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();
        int countRequests = updateRequest.getRequestIds().size();
        List<Request> requests = requestRepository.findByIdIn(updateRequest.getRequestIds());
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException(String.format("User with id=%d not found", userId));
        }
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException(String.format("Event with id=%d was not found", eventId)));

        if (event.getInitiator().getId() != userId) {
            throw new ObjectNotFoundException(String.format("Event with id=%d was not found", eventId));
        }
        for (Request request : requests) {
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                throw new RulesViolationException("Request status is not PENDING");
            }
        }
        if (updateRequest.getStatus() != null) {
            switch (updateRequest.getStatus()) {
                case "CONFIRMED":
                    if (event.getParticipantLimit() == 0 || !event.getRequestModeration()
                            || event.getParticipantLimit() > event.getConfirmedRequests() + countRequests) {
                        requests.forEach(request -> request.setStatus(RequestStatus.CONFIRMED));
                        event.setConfirmedRequests(event.getConfirmedRequests() + countRequests);
                        confirmedRequests.addAll(requests);

                    } else if (event.getParticipantLimit() <= event.getConfirmedRequests()) {
                        throw new RulesViolationException("Participant Limit");
                    } else {
                        for (Request request : requests) {
                            if (event.getParticipantLimit() > event.getConfirmedRequests()) {
                                request.setStatus(RequestStatus.CONFIRMED);
                                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                                confirmedRequests.add(request);
                            } else {
                                request.setStatus(RequestStatus.REJECTED);
                                rejectedRequests.add(request);
                            }
                        }
                    }
                    break;
                case "REJECTED":
                    requests.forEach(request -> request.setStatus(RequestStatus.REJECTED));
                    rejectedRequests.addAll(requests);
            }
        }

        eventRepository.save(event);
        requestRepository.saveAll(requests);

        List<ParticipationRequestDto> confirmed = confirmedRequests.stream()
                .map(x -> requestMapper.toDto(x))
                .collect(Collectors.toList());
        List<ParticipationRequestDto> rejected = rejectedRequests.stream()
                .map(x -> requestMapper.toDto(x))
                .collect(Collectors.toList());
        updateResult = new EventRequestStatusUpdateResult(confirmed, rejected);
        log.info(String.format("Update states (%s) of event's (id=%d) requests.", updateRequest.getStatus(), eventId));
        return updateResult;
    }

    @Override
    public List<EventFullDto> getEventsAdmin(List<Long> users, List<String> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable) {

        Specification<Event> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (users != null) {
                CriteriaBuilder.In<Long> usersInClause = criteriaBuilder.in(root.get("initiator"));
                for (Long user : users) {
                    usersInClause.value(user);
                }
                predicates.add(usersInClause);
            }
            if (states != null) {
//                List<String> allState = new ArrayList<>();
//                for (EventState state : EventState.values()) {
//                    allState.add(String.valueOf(state));
//                }
//                CriteriaBuilder.In<String> statesInClause = criteriaBuilder.in(root.get("state"));
//                for (String state : states) {
//                    if (!allState.contains(state)) {
//                        throw new InvalidRequestException("states must be: PENDING or PUBLISHED or CANCELED");
//                    }
//                    statesInClause.value(state);
//                }
                EventState eventState;
                List<EventState> eventStates = new ArrayList<>();
                try {
                    for (String state : states) {
                        eventState = EventState.valueOf(state);
                        eventStates.add(eventState);
                    }
                } catch (IllegalArgumentException e) {
                    throw new InvalidRequestException("Unknown parameter of state");
                }
                CriteriaBuilder.In<EventState> statesInClause = criteriaBuilder.in(root.get("state"));
                for (EventState st : eventStates) {
                    statesInClause.value(st);
                }
                predicates.add(statesInClause);
            }

            if (categories != null) {
                CriteriaBuilder.In<Long> categoriesInClause = criteriaBuilder.in(root.get("category"));
                for (Long category : categories) {
                    categoriesInClause.value(category);
                }
                predicates.add(categoriesInClause);
            }
            if (rangeStart != null) {
                predicates.add(criteriaBuilder.greaterThan(root.get("eventDate"), rangeStart));
            }
            if (rangeEnd != null) {
                predicates.add(criteriaBuilder.lessThan(root.get("eventDate"), rangeEnd));
            }
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
        List<Event> events = eventRepository.findAll(specification, pageable).getContent();

        return events.stream().map(x -> eventMapper.toFull(x, 0L)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateEventAdmin(long eventId, UpdateEventAdminRequest updateEventAdmin) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException(String.format("Event with id=%d was not found", eventId)));
        if (updateEventAdmin.getStateAction() != null) {
            if (updateEventAdmin.getStateAction().equals("PUBLISH_EVENT")) {
                if (!String.valueOf(event.getState()).equals("PENDING")) {
                    throw new RulesViolationException(String.format("Event have state=%s but must have state PENDING", event.getState()));
                }
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (updateEventAdmin.getStateAction().equals("REJECT_EVENT")) {
                if (String.valueOf(event.getState()).equals("PUBLISHED")) {
                    throw new RulesViolationException(String.format("Event have state=%s adn can't be REJECT", event.getState()));
                }
                event.setState(EventState.CANCELED);
            } else {
                throw new RulesViolationException("StateAction must be PUBLISH_EVENT or REJECT_EVENT");
            }
        }

        if (updateEventAdmin.getAnnotation() != null) {
            event.setAnnotation(updateEventAdmin.getAnnotation());
        }
        if (updateEventAdmin.getCategory() != 0) {
            Category category = categoryRepository.findById(updateEventAdmin.getCategory()).orElseThrow(() -> new ObjectNotFoundException(String.format("Category with id=%d was not found", updateEventAdmin.getCategory())));
            event.setCategory(category);
        }
        if (updateEventAdmin.getDescription() != null) {
            event.setDescription(updateEventAdmin.getDescription());
        }

        if (updateEventAdmin.getEventDate() != null) {
            if (updateEventAdmin.getEventDate().isBefore(LocalDateTime.now().minusHours(1))) {
                throw new InvalidRequestException("EventDate must be 1 hour earlier then time of publication");
            }
            event.setEventDate(updateEventAdmin.getEventDate());
        }
        if (updateEventAdmin.getLocation() != null) {
            event.setLon(updateEventAdmin.getLocation().getLon());
            event.setLat(updateEventAdmin.getLocation().getLat());
        }
        if (updateEventAdmin.getPaid() != null) {
            event.setPaid(updateEventAdmin.getPaid());
        }
        if (updateEventAdmin.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdmin.getParticipantLimit());
        }
        if (updateEventAdmin.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdmin.getRequestModeration());
        }
        if (updateEventAdmin.getTitle() != null) {
            event.setTitle(updateEventAdmin.getTitle());
        }
        event.setId(eventId);
        eventRepository.save(event);
//        Map <Long, Long> views = eventStatService.getEventsViews(List.of(eventId));
//        return eventMapper.toFull(event, views.get(event.getId()));
        return eventMapper.toFull(event, 0L);
    }

    @Override
    public List<EventShortDto> getEventsPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size, HttpServletRequest request) {
        StatsDto statsDto = new StatsDto();
        statsDto.setIp(request.getRemoteAddr());
        statsDto.setUri(request.getRequestURI());
        statsDto.setApp("ewm-main-service");
        statsDto.setTimestamp(LocalDateTime.now());
        client.saveStats(statsDto);
//        StatsDto statisticInDto = new StatsDto("ewm-service", request.getRequestURI(), request.getRemoteAddr(),
//                LocalDateTime.now());
//        statisticClient.postHit(statisticInDto);

        Specification<Event> specification = (((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (text != null) {
                predicates.add(criteriaBuilder.or(criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), "%" + text.toLowerCase() + "%"), criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + text.toLowerCase() + "%")));
            }
            if (categories != null) {
                CriteriaBuilder.In<Long> categoriesInClause = criteriaBuilder.in(root.get("category"));
                for (Long category : categories) {
                    categoriesInClause.value(category);
                }
                predicates.add(categoriesInClause);
            }
            if (paid != null) {
                predicates.add(criteriaBuilder.equal(root.get("paid"), paid));
            }
            if (rangeStart == null && rangeEnd == null) {
                predicates.add(criteriaBuilder.greaterThan(root.get("eventDate"), LocalDateTime.now()));
            } else {
                if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
                    throw new InvalidRequestException("rangeStart can't be after rangeEnd");
                } else if (rangeStart != null) {
                    predicates.add(criteriaBuilder.greaterThan(root.get("eventDate"), rangeStart));
                } else {
                    predicates.add(criteriaBuilder.lessThan(root.get("eventDate"), rangeEnd));
                }
            }
            if (onlyAvailable != null) {
                predicates.add(criteriaBuilder.lessThan(root.get("confirmedRequests"), root.get("participantLimit")));
            }
            predicates.add(criteriaBuilder.equal(root.get("state"), EventState.PUBLISHED));

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }));

        if (sort == null) {
            Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));
            List<Event> allEvents = eventRepository.findAll(specification, pageable).getContent();
//            Map<Long, Long> eventViews = eventStatService.getEventsViews(allEvents.stream().map(Event::getId).collect(Collectors.toList()));
            return allEvents.stream().map(x -> eventMapper.toShort(x, 0L))//client.getStats(String.valueOf(rangeStart), String.valueOf(rangeEnd), Collections.singletonList("/event/" + x.getId()), false).getHits()))
                    .collect(Collectors.toList());
        } else if (sort.equals(String.valueOf(EventSort.EVENT_DATE))) {
            Pageable pageable = PageRequest.of(from / size, size, Sort.by("eventDate"));
            List<Event> allEvents = eventRepository.findAll(specification, pageable).getContent();
            //  Map<Long, Long> eventViews = eventStatService.getEventsViews(allEvents.stream().map(Event::getId).collect(Collectors.toList()));
            return allEvents.stream().map(x -> eventMapper.toShort(x, 0L)).collect(Collectors.toList());
        } else if (sort.equals(String.valueOf(EventSort.VIEWS))) {
            Pageable pageable = PageRequest.of(from / size, size, Sort.unsorted());
            List<Event> allEvents = eventRepository.findAll(specification, pageable).getContent();
            //  Map<Long, Long> eventViews = eventStatService.getEventsViews(allEvents.stream().map(Event::getId).collect(Collectors.toList()));
            return allEvents.stream().map(x -> eventMapper.toShort(x, 0L)).sorted(Comparator.comparing(EventShortDto::getViews)).collect(Collectors.toList());
        }

        throw new InvalidRequestException("Sort cat be EVENT_DATE or VIEWS");
    }

    @Override
    public EventFullDto getEventByIdPublic(long id, HttpServletRequest request) {
        StatsDto statsDto = new StatsDto();
        statsDto.setIp(request.getRemoteAddr());
        statsDto.setUri(request.getRequestURI());
        statsDto.setApp("ewm-main-service");
        statsDto.setTimestamp(LocalDateTime.now());
        client.saveStats(statsDto);

        Event event = eventRepository.findByIdAndState(id, EventState.PUBLISHED)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Event with id=%d was not found", id)));

        List<String> uris = new ArrayList<>();
        uris.add(request.getRequestURI());

        Long view = client.getStats(
                LocalDateTime.now().minusDays(100).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                uris,
                true).get(0).getHits();

        return eventMapper.toFull(event, view);
    }
}
