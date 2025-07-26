package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {

    Item getItemById(Long userId, Long itemId);
    Collection<ItemOwnerDto> getItemOwner(Long userId);
    Item addNewItem(Long userId, NewItemDto item);
    Item updateItem(Long userId, Long itemId, UpdateItemDto item);
    Collection<Item> searchItem(Long userId, String text);
}
