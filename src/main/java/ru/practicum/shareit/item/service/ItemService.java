package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto getItemById(Long userId, Long itemId);

    Collection<ItemOwnerDto> getItemOwner(Long userId);

    ItemDto addNewItem(Long userId, NewItemDto item);

    ItemDto updateItem(Long userId, Long itemId, UpdateItemDto item);

    Collection<ItemDto> searchItem(Long userId, String text);
}
