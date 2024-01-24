package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exeption.ObjectNotFoundException;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImp implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImp.class);
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto addUserAdmin(UserDto userDto) {
        log.info("Add new user");
        return userMapper.toDto(userRepository.save(userMapper.toUser(userDto)));
    }

    @Override
    public List<UserDto> getUsersAdmin(List<Long> ids, Pageable pageable) {
        log.info("Get list of users by ids");
        if (ids == null || ids.isEmpty()) {
            return userRepository.findAll(pageable).stream()
                    .map(userMapper::toDto)
                    .collect(Collectors.toList());
        }
        return userRepository.findByIdIn(ids, pageable).stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUserAdmin(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException(String.format("User with id=%d was not found", userId));
        }
        userRepository.deleteById(userId);
        log.info(String.format("Deleted user with id=%d", userId));
    }
}