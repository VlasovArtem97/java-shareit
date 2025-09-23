package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.interfacemarker.Create;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@NotNull @Positive @RequestHeader(USER_ID) Long userId,
                                                 @Validated(Create.class) @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestClient.addItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestOwner(@NotNull @Positive @RequestHeader(USER_ID) Long userId) {
        return itemRequestClient.getItemRequestOwner(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequest(@NotNull @Positive @RequestHeader(USER_ID) Long userId) {
        return itemRequestClient.getAllItemRequest(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @NotNull @Positive @PathVariable Long requestId) {
        return itemRequestClient.getItemRequest(userId, requestId);
    }
}

