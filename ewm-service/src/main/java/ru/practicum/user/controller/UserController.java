package ru.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserWithSubscribers;
import ru.practicum.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/admin/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUserAdmin(@Valid @RequestBody UserDto userDto) {
        return userService.addUserAdmin(userDto);
    }

    @GetMapping("/admin/users")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getUsersAdmin(@RequestParam(required = false) List<Long> ids,
                                       @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
                                       @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {
        return userService.getUsersAdmin(ids, PageRequest.of(from / size, size));
    }

    @DeleteMapping("/admin/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserAdmin(@PathVariable long userId) {
        userService.deleteUserAdmin(userId);
    }

    @PostMapping(value = "/users/{userId}/subscriptions/{authorId}")
    @ResponseStatus(HttpStatus.CREATED)
    public UserWithSubscribers addSubscriber(@PathVariable long userId, @PathVariable long authorId) {
        return userService.addSubscriber(userId, authorId);
    }

    @DeleteMapping(value = "/users/{userId}/subscriptions/{authorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSubscriber(@PathVariable long userId, @PathVariable long authorId) {
        userService.deleteSubscriber(userId, authorId);
    }

    @GetMapping("/users/{userId}/subscriptions")
    @ResponseStatus(HttpStatus.OK)
    public UserWithSubscribers getUserWithSubscribers(@PathVariable long userId,
                                                      @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
                                                      @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {
        return userService.getUserWithSubscribers(userId, PageRequest.of(from / size, size));
    }
}