package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final HashMap<Long, Item> items = new HashMap<>();

    @Override
    public Optional<Item> getItemById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Collection<Item> getItemOwner(Long userId) {
        return items.values().stream()
                .filter(item -> item.getUserId().equals(userId))
                .toList();
    }

    @Override
    public Item addNewItem(Long userId, Item item) {
        item.setId(getNextId());
        item.setUserId(userId);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Long itemId, Item item) {
        items.put(itemId, item);
        return items.get(itemId);
    }

    @Override
    public Collection<Item> searchItem(String text) {
        return items.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase()))
                        || (item.getDescription().toLowerCase().contains(text.toLowerCase())))
                .filter(item -> item.getAvailable().equals(Boolean.TRUE))
                .toList();
    }

    private long getNextId() {
        long currentMaxId = items.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
