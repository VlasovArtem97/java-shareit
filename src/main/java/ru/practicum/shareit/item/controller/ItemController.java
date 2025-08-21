package ru.practicum.shareit.item.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.interfacemarker.Create;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemSearch;
import ru.practicum.shareit.item.service.ItemBookingCommitService;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemService itemService;
    private final ItemBookingCommitService itemBookingCommitService;

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@Positive @RequestHeader("X-Sharer-User-Id") Long userId, @Positive @PathVariable Long itemId) {
        return itemBookingCommitService.getItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getItemOwner(@Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemBookingCommitService.getItemOwnerById(userId);
    }

    @PostMapping
    public ItemDto addNewItem(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                              @Validated(Create.class) @RequestBody ItemDto item) {
        return itemService.addNewItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@Positive @RequestHeader("X-Sharer-User-Id") Long userId, @Positive @PathVariable Long itemId,
                              @RequestBody ItemDto item) {
        return itemService.updateItem(userId, itemId, item);
    }

    @GetMapping("/search")
    public Collection<ItemSearch> searchItem(@Positive @RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam String text) {
        return itemService.searchItem(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                 @Positive @PathVariable Long itemId,
                                 @Validated(Create.class) @RequestBody CommentDto newCommentDto) {
        return itemBookingCommitService.addComment(userId, itemId, newCommentDto);
    }
}
