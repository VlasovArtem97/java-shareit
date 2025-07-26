package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {

    Optional<Item> getItemById(Long itemId);
    Collection<Item> getItemOwner(Long userId);
    Item addNewItem(Long userId, Item item);
    Item updateItem(Long itemId, Item item);
    Collection<Item> searchItem(String text);
}
