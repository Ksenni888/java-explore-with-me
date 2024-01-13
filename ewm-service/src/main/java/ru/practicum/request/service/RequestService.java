package ru.practicum.request.service;


import ru.practicum.request.dto.ParticipationRequestDto;

public interface RequestService {
    ParticipationRequestDto addRequest(long userId, long eventId);
}
