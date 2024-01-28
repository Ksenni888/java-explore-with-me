package ru.practicum.user.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserWithSubscribers;

import java.util.List;

public interface UserService {
    UserDto addUserAdmin(UserDto userDto);

    List<UserDto> getUsersAdmin(List<Long> ids, Pageable pageable);

    void deleteUserAdmin(long userId);

    UserWithSubscribers addSubscriber(long userId, long authorId);

    void deleteSubscriber(long userId, long authorId);

    UserWithSubscribers getUserWithSubscribers(long userId, Pageable pageable);
}