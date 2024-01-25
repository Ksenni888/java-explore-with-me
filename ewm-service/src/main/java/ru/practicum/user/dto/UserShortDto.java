package ru.practicum.user.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UserShortDto {
    @NotNull
    private long id;

    @NotNull
    private String name;
}