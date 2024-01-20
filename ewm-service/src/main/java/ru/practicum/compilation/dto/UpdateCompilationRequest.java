package ru.practicum.compilation.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
public class UpdateCompilationRequest {
    @Size(min = 1, max = 50)
    private String title;
    private Boolean pinned;
    private List<Long> events;
}
