package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAll();

    User save(User user);

    User updateUser(User user);

    Optional<User> findUserById(Long userId);

    void deleteUser(Long userId);
}
