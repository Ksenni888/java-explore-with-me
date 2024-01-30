package ru.practicum.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.dto.UserWithSubscribers;
import ru.practicum.user.model.User;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    public User toUser(UserDto userDto) {
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }

    public UserDto toDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public UserShortDto toShort(User user) {
        UserShortDto userShortDto = new UserShortDto();
        userShortDto.setId(user.getId());
        userShortDto.setName(user.getName());
        return userShortDto;
    }

    public UserWithSubscribers toUserDtoWithSubscribers(User user) {
        UserWithSubscribers userWithSubscribers = new UserWithSubscribers();
        userWithSubscribers.setId(user.getId());
        userWithSubscribers.setName(user.getName());
        userWithSubscribers.setEmail(user.getEmail());
        userWithSubscribers.setSubscribers(user.getSubscriptions().isEmpty() ? new ArrayList<>() :
                user.getSubscriptions().stream()
                        .map(this::toDto)
                        .collect(Collectors.toList()));
        return userWithSubscribers;
    }
}