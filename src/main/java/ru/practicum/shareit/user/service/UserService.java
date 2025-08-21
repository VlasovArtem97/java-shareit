package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto saveUser(UserDto user);

    UserDto updateUser(Long userId, UserDto user);

    UserDto findUserById(Long userId);

    void deleteUser(Long userId);

    User returnUserFindById(Long userId);
}
