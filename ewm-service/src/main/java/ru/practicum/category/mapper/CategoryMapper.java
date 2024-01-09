package ru.practicum.category.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.model.Category;

@Component
@RequiredArgsConstructor
public class CategoryMapper {

    public Category toCategory(CategoryDto categoryDto) {
        Category category = new Category();
        category.setName(categoryDto.getName());
        return category;
    }

    public CategoryDto toDto(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        return categoryDto;
    }

//    public CategoryDtoOut toDtoOut(Category category) {
//        CategoryDtoOut categoryDtoOut = new CategoryDtoOut();
//        categoryDtoOut.setId(category.getId());
//        categoryDtoOut.setName(category.getName());
//        return categoryDtoOut;
//    }
}