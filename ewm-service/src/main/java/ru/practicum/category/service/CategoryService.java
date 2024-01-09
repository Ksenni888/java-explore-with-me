package ru.practicum.category.service;

import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.category.dto.CategoryDto;

public interface CategoryService {
    CategoryDto addCategory(CategoryDto categoryDto);
    void deleteCategory(long catId);

    CategoryDto updateCategory(@PathVariable long catId, CategoryDto categoryDto);
}
