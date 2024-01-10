package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exeption.ObjectNotFoundException;
import ru.practicum.exeption.TimeViolationException;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.model.User;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;

    public EventFullDto addEvent(long userId, NewEventDto newEventDto) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new TimeViolationException("Field: eventDate. " +
                    "Error: должно содержать дату, которая еще не наступила. Value:" + newEventDto.getEventDate());
        }
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException(String.format("User with id=%d not found", userId)));
        UserShortDto userShortDto = userMapper.toShort(user);
        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(
                () -> new ObjectNotFoundException(String.format("Category with id=%d not found", newEventDto.getCategory()))
        );
        CategoryDto categoryDto = categoryMapper.toDto(category);
        //  if (newEventDto.getRequestModeration())
       //   Event event = new Event();

        return eventMapper.toFull(eventRepository.save(eventMapper.toEvent(newEventDto, category, 0,
                LocalDateTime.now(), userId, user, null )), categoryDto, userShortDto, 0);
    }
}