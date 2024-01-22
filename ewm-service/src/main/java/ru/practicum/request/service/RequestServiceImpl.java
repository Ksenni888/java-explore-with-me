package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exeption.ObjectNotFoundException;
import ru.practicum.exeption.RulesViolationException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private static final Logger log = LoggerFactory.getLogger(RequestServiceImpl.class);
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;

    @Override
    @Transactional
    public ParticipationRequestDto addRequestPrivate(long userId, long eventId) {
        Request request = new Request();
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException(String.format("Event with id=%d was not found", eventId)));
        User user = checkUser(userId);
        List<Request> requests = requestRepository.findAllByRequesterIdAndEventId(userId, eventId);
        if (requests.size() != 0) {
            throw new RulesViolationException("Your request added");
        }
        if (userId == event.getInitiator().getId()) {
            throw new RulesViolationException("Event's ownew can't be add");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new RulesViolationException("Can't add in not published event");
        }
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit().equals(event.getConfirmedRequests())) {
            throw new RulesViolationException("Participant Limit");
        }

        request.setRequester(user);
        request.setEvent(event);
        request.setStatus(RequestStatus.PENDING);
        request.setCreated(LocalDateTime.now());

        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            request.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }
        log.info("Add request by user");
        return requestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getRequestsPrivate(long userId) {
        checkUser(userId);
        log.info("Get user requests to event");
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(requestMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        checkUser(userId);
        Request request = requestRepository.findById(requestId).orElseThrow(
                () -> new ObjectNotFoundException(String.format("Request with id=%d not found", requestId)));
        request.setStatus(RequestStatus.CANCELED);
        request = requestRepository.save(request);
        log.info("Canceled request");
        return requestMapper.toDto(request);
    }

    public User checkUser(long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new ObjectNotFoundException(String.format("User with id=%d not found", id)));
    }
}