package ru.practicum.request.service;


import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto addRequestPrivate(long userId, long eventId);

    List<ParticipationRequestDto> getRequestsPrivate(long userId);

    ParticipationRequestDto cancelRequest(long userId, long requestId);


}
