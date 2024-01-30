package ru.practicum.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserWithSubscribers {
    private long id;
    private String name;
    private String email;
    private List<UserDto> subscribers;
}
