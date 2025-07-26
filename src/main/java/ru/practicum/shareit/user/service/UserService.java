package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreate;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdate;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserDto> getAllUsers();
    UserDto saveUser(UserCreate user);
    UserDto updateUser(Long userId, UserUpdate user);
    UserDto findUserById(Long userId);
    void deleteUser(Long userId);
}
