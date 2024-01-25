package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exeption.ObjectNotFoundException;
import ru.practicum.exeption.RulesViolationException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CategoryDto addCategory(CategoryDto categoryDto) {
        log.info("Add new category");
        return categoryMapper.toDto(categoryRepository.save(categoryMapper.toCategory(categoryDto)));
    }

    @Override
    @Transactional
    public void deleteCategory(long categoryId) {
        getCategoryOrThrow(categoryId);
        if (eventRepository.findAllByCategoryId(categoryId) != null) {
            throw new RulesViolationException(
                    String.format("Category with id=%d can't remove, because have events", categoryId));
        }
        categoryRepository.deleteById(categoryId);
        log.info("Category was deleted");
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(long categoryId, CategoryDto categoryDto) {
        Category category = getCategoryOrThrow(categoryId);
        category.setName(categoryDto.getName());
        log.info("Update category");
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public List<CategoryDto> getCategories(Pageable pageable) {
        List<CategoryDto> allCategories = categoryRepository.findAll(pageable).stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
        log.info("Find all categories with parameters");
        return allCategories;
    }

    @Override
    public CategoryDto getCategory(long categoryId) {
        Category category = getCategoryOrThrow(categoryId);
        log.info("Find category by id");
        return categoryMapper.toDto(category);
    }

    private Category getCategoryOrThrow(long id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new ObjectNotFoundException(
                        String.format("Category with id=%d was not found", id)));
    }
}