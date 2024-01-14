package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exeption.ObjectNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final EventRepository eventRepository;
    private final CompilationRepository compilationRepository;
    private final EventMapper eventMapper;

    @Override
    @Transactional
    public CompilationDto addCompilationAdmin(NewCompilationDto newCompilationDto) {
        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setTitle(newCompilationDto.getTitle());
        compilationDto.setPinned(newCompilationDto.getPinned());

        List<Event> events = eventRepository.findByIdIn(newCompilationDto.getEvents());
        Compilation compilation = new Compilation();
        compilation.setTitle(newCompilationDto.getTitle());
        compilation.setPinned(newCompilationDto.getPinned());
        compilation.setEvents(events);

        List<EventShortDto> eventShortDtos = events.stream()
                .map(x -> eventMapper.toShort(x, 0L))
                .collect(Collectors.toList());
        compilationDto.setEvents(eventShortDtos);

        return compilationDto;
    }

    public void deleteCompilationAdmin(long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new ObjectNotFoundException(String.format("Compilation with id=%d not found", compId));
        }

        compilationRepository.deleteById(compId);
    }

    public CompilationDto updateCompilationAdmin(long compId, UpdateCompilationRequest updateCompilationRequest) {
        CompilationDto compilationDto = new CompilationDto();
        Compilation compilation = new Compilation();
        if (updateCompilationRequest.getEvents() != null) {
            List<Event> events = eventRepository.findByIdIn(updateCompilationRequest.getEvents());
            compilation.setEvents(events);
            List<EventShortDto> shortDto = events.stream()
                    .map(x -> eventMapper.toShort(x, 0L))
                    .collect(Collectors.toList());
            compilationDto.setEvents(shortDto);
        }
        if (updateCompilationRequest.getTitle() != null) {
            compilationDto.setTitle(updateCompilationRequest.getTitle());

        }
        if (updateCompilationRequest.getPinned() != null) {
            compilationDto.setPinned(updateCompilationRequest.getPinned());
        }
        Compilation compilation1 = compilationRepository.save(compilation);
        compilationDto.setId(compilation1.getId());
        return compilationDto;
    }
}
