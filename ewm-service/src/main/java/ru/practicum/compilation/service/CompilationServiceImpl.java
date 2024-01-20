package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exeption.ObjectNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private static final Logger log = LoggerFactory.getLogger(CompilationServiceImpl.class);
    private final EventRepository eventRepository;
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;

    @Override
    @Transactional
    public CompilationDto addCompilationAdmin(NewCompilationDto newCompilationDto) {
        Compilation compilation = new Compilation();
        compilation.setTitle(newCompilationDto.getTitle());

        if (newCompilationDto.getEvents() != null) {
            List<Event> events = eventRepository.findAllByIdIn(newCompilationDto.getEvents());
            compilation.setEvents(events);
        } else {
            compilation.setEvents(new ArrayList<>());
        }

        if (newCompilationDto.getPinned() == null) {
            compilation.setPinned(false);
        } else {
            compilation.setPinned(newCompilationDto.getPinned());
        }

        compilationRepository.save(compilation);
        return compilationMapper.toDto(compilation);
    }

    @Override
    @Transactional
    public void deleteCompilationAdmin(long compId) {
        if (compilationRepository.existsById(compId)) {
            compilationRepository.deleteById(compId);
            log.info(String.format("Compilation with id=%d not found", compId));
        }
    }

    @Override
    @Transactional
    public CompilationDto updateCompilationAdmin(long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new ObjectNotFoundException(String.format("Compilation with id=%d not found", compId))
        );
        if (updateCompilationRequest.getEvents() != null && !updateCompilationRequest.getEvents().isEmpty()) {
            List<Event> events = eventRepository.findByIdIn(updateCompilationRequest.getEvents());
            compilation.setEvents(events);
        }
        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());

        }
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        compilationRepository.save(compilation);
        return compilationMapper.toDto(compilation);
    }

    @Override
    public List<CompilationDto> findCompilationsPublic(Boolean pinned, Pageable pageable) {
        if (pinned == null) {
            return compilationRepository.findAll(pageable).stream()
                    .map(x -> compilationMapper.toDto(x))
                    .collect(Collectors.toList());
        }

        return compilationRepository.findAllByPinned(pinned, pageable).stream()
                .map(x -> compilationMapper.toDto(x))
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto findCompilationPublic(long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new ObjectNotFoundException(String.format("Compilation with id=%d was not found", compId)));
        return compilationMapper.toDto(compilation);
    }
}