package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class RequestController {
    private final RequestService requestService;

    @PostMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addRequestPrivate(@PathVariable long userId,
                                                     @RequestParam long eventId) {
        return requestService.addRequestPrivate(userId, eventId);
    }

    @GetMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequestsPrivate(@PathVariable long userId) {
        return requestService.getRequestsPrivate(userId);
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelRequest(@PathVariable long userId,
                                                 @PathVariable long requestId) {
        return requestService.cancelRequest(userId, requestId);

    }
}
