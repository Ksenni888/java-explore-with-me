package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exeption.InvalidRequestException;
import ru.practicum.exeption.ObjectNotFoundException;
import ru.practicum.exeption.RulesViolationException;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private static final Logger log = LoggerFactory.getLogger(EventServiceImpl.class);
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public EventFullDto addEventPrivate(long userId, NewEventDto newEventDto) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new RulesViolationException("Field: eventDate. " +
                    "Error: должно содержать дату, которая еще не наступила. Value:" + newEventDto.getEventDate());
        }
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException(String.format("User with id=%d not found", userId)));
        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(
                () -> new ObjectNotFoundException(String.format("Category with id=%d not found", newEventDto.getCategory()))
        );
        Event event = eventMapper.toEvent(newEventDto, category, 0,
                LocalDateTime.now(), userId, user, null);
        event = eventRepository.save(event);

        CategoryDto categoryDto = categoryMapper.toDto(category);
        UserShortDto userShortDto = userMapper.toShort(user);
        log.info("Add new event");
        return eventMapper.toFull(event, 0L);
    }

    @Override
    public List<EventShortDto> getEventPrivate(long userId, Pageable pageable) {
        log.info("Get owner events");
        return eventRepository.findAllByInitiatorId(userId, pageable).stream()
                .map(x -> eventMapper.toShort(x, categoryMapper.toDto(x.getCategory()), userMapper.toShort(x.getInitiator()), 0))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventByIdPrivate(long userId, long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException(String.format("Event with id=%d was not found", eventId)));
        if (event.getInitiator().getId() != userId) {
            throw new ObjectNotFoundException(String.format("Event with id=%d added another user", eventId));
        }
        UserShortDto userShortDto = userMapper.toShort(userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException(String.format("User with id=%d was not found", userId))));
        CategoryDto categoryDto = categoryMapper.toDto(event.getCategory());
        log.info("Get information about event for owner");
        return eventMapper.toFull(eventRepository.findByInitiatorIdAndId(userId, eventId), 0L);
    }

    @Override
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
                throw new RulesViolationException("Field: eventDate. " +
                        "Error: должно содержать дату, которая еще не наступила. Value:" + event.getEventDate());
            } else {
                event.setEventDate(updateEvent.getEventDate());
            }
        }
        if (updateEvent.getCategory() != 0) {
            long categoryId = updateEvent.getCategory();
            Category category = categoryRepository.findById(categoryId).orElseThrow(
                    () -> new ObjectNotFoundException(String.format("Category with id=%d was not found", categoryId)));
            event.setCategory(category);
        }
        if (updateEvent.getAnnotation() != null) {
            event.setAnnotation(updateEvent.getAnnotation());
        }
        if (updateEvent.getDescription() != null) {
            event.setDescription(updateEvent.getDescription());
        }
        if (updateEvent.getLocation().getLat() != null) {
            event.setLat(updateEvent.getLocation().getLat());
        }
        if (updateEvent.getLocation().getLon() != null) {
            event.setLon(updateEvent.getLocation().getLon());
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

        return eventMapper.toFull(eventRepository.save(event), 0L);
    }

    public List<EventFullDto> getEventsAdmin(List<Long> users,
                                             List<String> states,
                                             List<Long> categories,
                                             LocalDateTime rangeStart,
                                             LocalDateTime rangeEnd,
                                             Pageable pageable) {

        Specification<Event> specification = new Specification<Event>() {
            @Override
            public Predicate toPredicate(Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (users != null) {
                    CriteriaBuilder.In<Long> usersInClause = criteriaBuilder.in(root.get("initiator"));
                    for (Long user : users) {
                        usersInClause.value(user);
                    }
                    predicates.add(usersInClause);
                }
                if (states != null) {
                    List<String> allState = new ArrayList<>();
                    for (EventState state : EventState.values()) {
                        allState.add(String.valueOf(state));
                    }
                    CriteriaBuilder.In<String> statesInClause = criteriaBuilder.in(root.get("state"));
                    for (String state : states) {
                        if (!allState.contains(state)) {
                            throw new InvalidRequestException("states must be: PENDING or PUBLISHED or CANCELED");
                        }
                        statesInClause.value(state);
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
            }
        };
        List<Event> events = eventRepository.findAll(specification, pageable).getContent();
        Map<Long, Long> views = new HashMap<>();

        return events.stream().map(x -> eventMapper.toFull(x, views.get(x.getId())))
                .collect(Collectors.toList());
    }
}