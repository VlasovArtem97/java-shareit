package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreate;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdate;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {
    private final UserRepository repository;

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
        User userCreate = UserMapper.mapToNewUser(user);
        repository.save(userCreate);
        log.debug("Новый пользователь: {}", userCreate);
        return UserMapper.mapToUserDto(userCreate);
    }

    @Override
    public UserDto updateUser(Long userId, UserUpdate user) {
        log.info("Получен запрос на обновления данных пользователя: {}", user);
        User oldUser = findUserById(userId);
        User userMapUpdate = UserMapper.mapToUpdateUser(user, oldUser);
        User userUpdate = repository.updateUser(userMapUpdate);
        log.debug("Обновленный пользователь: {}", userUpdate);
        return UserMapper.mapToUserDto(userUpdate);
    }

    @Override
    public void deleteUser(Long userId) {
        findUserById(userId);
        repository.deleteUser(userId);
    }

    @Override
    public User findUserById(Long userId) {
        log.info("Получен запрос на поиск пользователя по id - {}", userId);
        return repository.findUserById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " +
                userId + " не найден"));
    }
}
