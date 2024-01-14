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
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException(String.format("Event with id=%d was not found", eventId)));
        if (event.getInitiator().getId() == userId) {
            throw new RulesViolationException("Event's ownew can't be add");
        }
        if (event.getState() != EventState.PUBLISHED) {
            throw new RulesViolationException("Can't add in not published event");
        }
        if (event.getParticipantLimit() - event.getConfirmedRequests() == 0) {
            throw new RulesViolationException("Participant Limit");
        }
        if (requestRepository.findByEventAndRequester(eventId, userId) != null) {
            throw new RulesViolationException("Your request added");
        }
        Request request = new Request();
        if (!event.getRequestModeration()) {
            request.setStatus(RequestStatus.CONFIRMED);
            requestRepository.save(request);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        } else {
            request.setStatus(RequestStatus.PENDING);
            requestRepository.save(request);
        }

        request.setCreated(LocalDateTime.now());
        request.setRequester(event.getInitiator());
        request.setEvent(event);

        log.info("Add request by user");
        return requestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getRequestsPrivate(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException(String.format("User with id=%d not found", userId));
        }
        log.info("Get requests by user event");
        return requestRepository.findAllByRequester(userId).stream()
                .map(x -> requestMapper.toDto(x)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException(String.format("User with id=%d not found", userId));
        }
        Request request = requestRepository.findById(requestId).orElseThrow(
                () -> new ObjectNotFoundException(String.format("Request with id=%d not found", requestId)));
        request.setStatus(RequestStatus.CANCELED);
        requestRepository.save(request);
        log.info("Canceled request");
        return requestMapper.toDto(request);
    }
}