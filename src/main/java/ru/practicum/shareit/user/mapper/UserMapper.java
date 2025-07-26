package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserCreate;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdate;
import ru.practicum.shareit.user.model.User;

public class UserMapper {
    public static User mapToNewUser(UserCreate createUser) {
        User user = new User();
        user.setName(createUser.getName());
        user.setEmail(createUser.getEmail());
        return user;
    }

    public static User mapToUpdateUser(UserUpdate updateUser, User user) {
        if(updateUser.getName() != null && !updateUser.getName().isBlank()) {
            user.setName(updateUser.getName());
        }
        if(updateUser.getEmail() != null && !updateUser.getEmail().isBlank()) {
            user.setEmail(updateUser.getEmail());
        }
        return user;
    }

    public static UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }
}
