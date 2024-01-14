package ru.practicum.compilation.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.event.dto.EventShortDto;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
public class CompilationDto {
    private long id;
    private List<EventShortDto> events;
    private Boolean pinned;
    @NotBlank
    private String title;

}
