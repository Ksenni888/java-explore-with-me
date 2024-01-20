package ru.practicum.compilation.service;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    CompilationDto addCompilationAdmin(NewCompilationDto newCompilationDto);

    void deleteCompilationAdmin(@PathVariable long compId);

    CompilationDto updateCompilationAdmin(long compId, UpdateCompilationRequest updateCompilationRequest);

    List<CompilationDto> findCompilationsPublic(Boolean pinned, Pageable pageable);

    CompilationDto findCompilationPublic(long compId);
}