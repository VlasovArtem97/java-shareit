package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
public class UserRepositoryImpl implements UserRepository {
    private final HashMap<Long, User> users = new HashMap<>();

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User save(User user) {
        validateEmail(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        validateEmail(user);
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public Optional<User> findUserById(Long userId) {
        return users.values().stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst();
    }

    @Override
    public void deleteUser(Long userId) {
        users.remove(userId);
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validateEmail(User user) {
        Optional<User> userOptional = users.values().stream()
                .filter(user1 -> user1.getEmail().equalsIgnoreCase(user.getEmail())
                        && !user1.getId().equals(user.getId()))
                .findFirst();
        if(userOptional.isPresent()) {
            throw new ConflictException("Пользователь с email: " + user.getEmail() + " уже существует");
        }
    }

}
