package ru.practicum.compilation.service;

import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;

public interface CompilationService {

    CompilationDto addCompilationAdmin(NewCompilationDto newCompilationDto);

    void deleteCompilationAdmin(@PathVariable long compId);

    CompilationDto updateCompilationAdmin(long compId, UpdateCompilationRequest updateCompilationRequest);
}