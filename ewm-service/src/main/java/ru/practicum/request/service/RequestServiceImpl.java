package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
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

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private static final Logger log = LoggerFactory.getLogger(RequestServiceImpl.class);
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;

    @Override
    public ParticipationRequestDto addRequest(long userId, long eventId) {
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
        } else {
            request.setStatus(RequestStatus.PENDING);
        }

        request.setCreated(LocalDateTime.now());
        request.setRequester(event.getInitiator());
        request.setEvent(event);
        return requestMapper.toDto(requestRepository.save(request));
    }
}