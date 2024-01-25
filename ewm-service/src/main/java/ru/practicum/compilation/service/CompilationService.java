package ru.practicum.compilation.service;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    CompilationDto addCompilationAdmin(NewCompilationDto newCompilationDto);

    void deleteCompilationAdmin(long compilationId);

    CompilationDto updateCompilationAdmin(long compilationId, UpdateCompilationRequest updateCompilationRequest);

    List<CompilationDto> findCompilationsPublic(Boolean pinned, Pageable pageable);

    CompilationDto findCompilationPublic(long compilationId);
}