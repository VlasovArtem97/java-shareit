package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@Positive @RequestHeader("X-Sharer-User-Id") Long userId, @Positive @PathVariable Long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping
    public Collection<ItemOwnerDto> getItemOwner(@Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemOwner(userId);
    }

    @PostMapping
    public ItemDto addNewItem(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                              @Valid @RequestBody NewItemDto item) {
        return itemService.addNewItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@Positive @RequestHeader("X-Sharer-User-Id") Long userId, @Positive @PathVariable Long itemId,
                              @RequestBody UpdateItemDto item) {
        return itemService.updateItem(userId, itemId, item);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItem(@Positive @RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam String text) {
        return itemService.searchItem(userId, text);
    }
}
