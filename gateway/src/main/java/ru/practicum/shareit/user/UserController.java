package ru.practicum.shareit.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.interfacemarker.Create;
import ru.practicum.shareit.interfacemarker.Update;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Validated
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return userClient.getAllUsers();
    }

    @PostMapping
    public ResponseEntity<Object> saveNewUser(@Validated(Create.class) @RequestBody UserDto user) {
        return userClient.saveNewUser(user);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@NotNull @Positive @PathVariable Long userId,
                                             @Validated(Update.class) @RequestBody UserDto user) {
        return userClient.updateUser(userId, user);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findUserById(@NotNull @Positive @PathVariable Long userId) {
        return userClient.findUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@NotNull @Positive @PathVariable Long userId) {
        userClient.deleteUser(userId);
    }
}
