package ru.practicum.user.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UserDto {
    private long id;

    @Email
    @NotBlank
    @Size(min = 6, max = 254, message = "Email length can be 6 to 254")
    private String email;

    @NotBlank
    @Size(min = 2, max = 250, message = "Name can be 2 to 250")
    private String name;
}