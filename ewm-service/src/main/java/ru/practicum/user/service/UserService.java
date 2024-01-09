package ru.practicum.user.service;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.user.dto.UserDto;


import java.util.List;

public interface UserService {
    UserDto addUser(UserDto userDto);

    List<UserDto> getUsers(List<Long> ids, Pageable pageable);
    void deleteUser(@PathVariable long userId);
}
