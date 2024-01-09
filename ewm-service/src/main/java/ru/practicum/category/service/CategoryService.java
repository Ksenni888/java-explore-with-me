package ru.practicum.category.service;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.category.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(CategoryDto categoryDto);

    void deleteCategory(long catId);

    CategoryDto updateCategory(long catId, CategoryDto categoryDto);

    List<CategoryDto> getCategories(Pageable pageable);
    CategoryDto getCategory(long catId);
}
