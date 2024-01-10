package ru.practicum.category.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Setter
@Getter
public class CategoryDto {
    private long id;
    @NotBlank
    @Size(min = 1, max = 50)
    private String name;
}