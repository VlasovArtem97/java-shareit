package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreate;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdate;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAllUsers() {
        log.info("Получен запрос на получения списка всех пользователей");
        List<UserDto> userDtoList = repository.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
        log.debug("Список пользователей: {}", userDtoList);
        return userDtoList;
    }

    @Override
    public UserDto saveUser(UserCreate user) {
        log.info("Получен запрос на добавление пользователя: {}", user);
        User newUser = UserMapper.mapToNewUser(user);
        if (repository.existsByEmail(newUser.getEmail())) {
            log.error("При добавлении нового Пользователя, указанная электронная почта - {} уже существует",
                    newUser.getEmail());
            throw new ConflictException("Пользователь с email: " + newUser.getEmail() + " уже существует");
        }
        UserDto userDto = UserMapper.mapToUserDto(repository.save(newUser));
        log.debug("Новый пользователь: {}", userDto);
        return userDto;
    }

    @Override
    public UserDto updateUser(Long userId, UserUpdate user) {
        log.info("Получен запрос на обновления данных пользователя: {}", user);
        User oldUser = returnUserFindById(userId);
        if (user.getName() != null && !user.getName().isBlank()) {
            oldUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            if (!user.getEmail().equalsIgnoreCase(oldUser.getEmail())) {
                if (repository.existsByEmailAndIdNot(user.getEmail(), userId)) {
                    log.error("При обновлении данных Пользователя, указанная электронная почта - {} уже существует",
                            user.getEmail());
                    throw new ConflictException("Пользователь с email: " + user.getEmail() + " уже существует");
                }
            }
            oldUser.setEmail(user.getEmail());
        }
        User userUpdate = repository.save(oldUser);
        log.debug("Обновленный пользователь: {}", userUpdate);
        return UserMapper.mapToUserDto(userUpdate);
    }

    @Override
    public void deleteUser(Long userId) {
        log.info("Получен запрос на удаление пользователя с id - {}", userId);
        findUserById(userId);
        repository.deleteById(userId);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto findUserById(Long userId) {
        log.info("Получен запрос на поиск пользователя по id - {}", userId);
        User user = repository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " +
                userId + " не найден"));
        UserDto userDto = UserMapper.mapToUserDto(user);
        log.debug("Найден пользователь: {}", userDto);
        return userDto;
    }

    @Transactional(readOnly = true)
    @Override
    public User returnUserFindById(Long userId) {
        UserDto userDto = findUserById(userId);
        return UserMapper.mapToUserFromUserDto(userDto);
    }
}
