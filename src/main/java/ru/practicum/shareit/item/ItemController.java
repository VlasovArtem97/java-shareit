package ru.practicum.shareit.item;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public Item getItemById(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,@Positive  @PathVariable Long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping
    public Collection<ItemOwnerDto> getItemOwner(@Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemOwner(userId);
    }

    @PostMapping
    public Item addNewItem(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                    @RequestBody NewItemDto item) {
        return itemService.addNewItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@Positive @RequestHeader("X-Sharer-User-Id") Long userId, @Positive @PathVariable Long itemId,
                           @RequestBody UpdateItemDto item) {
        return itemService.updateItem(userId, itemId, item);
    }

    @GetMapping("/search")
    public Collection<Item> searchItem(@Positive @RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam String text) {
        return itemService.searchItem(userId, text);
    }
}
