package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.interfacemarker.Create;

import java.util.Collections;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;
    private static final String USER_ID = "X-Sharer-User-Id";

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@NotNull @Positive @RequestHeader(USER_ID) Long userId,
                                              @NotNull @Positive @PathVariable Long itemId) {
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemOwner(@NotNull @Positive @RequestHeader(USER_ID) Long userId) {
        return itemClient.getItemOwner(userId);
    }

    @PostMapping
    public ResponseEntity<Object> addNewItem(@NotNull @Positive @RequestHeader(USER_ID) Long userId,
                                             @Validated(Create.class) @RequestBody ItemDto item) {
        return itemClient.addNewItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@NotNull @Positive @RequestHeader(USER_ID) Long userId,
                                             @NotNull @Positive @PathVariable Long itemId,
                                             @RequestBody ItemDto item) {
        return itemClient.updateItem(userId, itemId, item);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@NotNull @Positive @RequestHeader(USER_ID) Long userId,
                                             @RequestParam String text) {
        if (text == null || text.isBlank()) {
            return ResponseEntity.ok().body(Collections.emptyList());
        } else {
            return itemClient.searchItem(userId, text);
        }
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@NotNull @Positive @RequestHeader(USER_ID) Long userId,
                                             @NotNull @Positive @PathVariable Long itemId,
                                             @Validated(Create.class) @RequestBody CommentDto newCommentDto) {
        return itemClient.addComment(userId, itemId, newCommentDto);
    }
}
