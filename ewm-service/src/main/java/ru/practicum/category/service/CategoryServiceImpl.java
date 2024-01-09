package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.exeption.ObjectNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto addCategory(CategoryDto categoryDto) {
        log.info("Add new category");
        return categoryMapper.toDto(categoryRepository.save(categoryMapper.toCategory(categoryDto)));
    }

    @Override
    @Transactional
    public void deleteCategory(long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new ObjectNotFoundException(
                    String.format("Category with id=%d was not found", catId));
        }
        categoryRepository.deleteById(catId);
        log.info("Category was deleted");
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(long catId, CategoryDto categoryDto) {
//        if (!categoryRepository.existsById(catId)) {
//            throw new ObjectNotFoundException(
//                    String.format("Category with id=%d was not found", catId));
//        }
        Category category = categoryRepository.findById(catId).orElseThrow(
                () -> new ObjectNotFoundException(
                String.format("Category with id=%d was not found", catId)));
        category.setName(categoryDto.getName());
        log.info("Update category");
        return categoryMapper.toDto(categoryRepository.save(category));
    }


}