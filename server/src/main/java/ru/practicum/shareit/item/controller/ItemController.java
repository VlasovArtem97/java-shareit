package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemSearch;
import ru.practicum.shareit.item.service.ItemBookingCommitRequestService;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemService itemService;
    private final ItemBookingCommitRequestService itemBookingCommitService;
    private static final String USER_ID = "X-Sharer-User-Id";

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader(USER_ID) Long userId, @PathVariable Long itemId) {
        return itemBookingCommitService.getItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getItemOwner(@RequestHeader(USER_ID) Long userId) {
        return itemBookingCommitService.getItemOwnerById(userId);
    }

    @PostMapping
    public ItemDto addNewItem(@RequestHeader(USER_ID) Long userId, @RequestBody ItemDto item) {
        return itemBookingCommitService.addNewItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(USER_ID) Long userId, @PathVariable Long itemId,
                              @RequestBody ItemDto item) {
        return itemService.updateItem(userId, itemId, item);
    }

    @GetMapping("/search")
    public Collection<ItemSearch> searchItem(@RequestHeader(USER_ID) Long userId, @RequestParam String text) {
        return itemService.searchItem(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(USER_ID) Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody CommentDto newCommentDto) {
        return itemBookingCommitService.addComment(userId, itemId, newCommentDto);
    }
}
