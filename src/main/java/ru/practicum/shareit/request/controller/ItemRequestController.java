package ru.practicum.shareit.request.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.interfacemarker.Create;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addItemRequest(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Validated(Create.class) @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.addItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getItemRequestOwner(@Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getItemRequestOwner(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequest(@Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getAllItemRequest(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequest(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Positive @PathVariable Long requestId) {
        return itemRequestService.getItemRequest(userId, requestId);
    }


}
