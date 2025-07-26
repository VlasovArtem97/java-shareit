package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    public Item getItemById(Long userId, Long itemId) {
        log.info("Получен запрос на получение Item по Id - {} от пользователя с id - {}", itemId, userId);
        userService.findUserById(userId);
        Item item = itemRepository.getItemById(itemId).orElseThrow(() -> new NotFoundException("Item c ID - " + itemId +
                " не найден"));
        log.info("Полученный предмет: {}", item);
        return item;
    }

    @Override
    public Collection<ItemOwnerDto> getItemOwner(Long userId) {
        log.info("Получен запрос на получения списка Item пользователя с id - {}", userId);
        userService.findUserById(userId);
        return itemRepository.getItemOwner(userId).stream()
                .map(ItemMapper::mapToItemOwnerDto)
                .toList();
    }

    @Override
    public Item addNewItem(Long userId, NewItemDto item) {
        log.info("Получен запрос на добавление нового Item: {}, пользователем с id - {}", item, userId);
        userService.findUserById(userId);
        Item itemNew = ItemMapper.mapToItem(item);
        Item itemCreate = itemRepository.addNewItem(userId, itemNew);
        log.info("Добавленный Item: {}", itemCreate);
        return itemCreate;
    }

    @Override
    public Item updateItem(Long userId, Long itemId, UpdateItemDto item) {
        log.info("Получен запрос на обновление данных Item: {}", item);
        Item itemOld = getItemById(userId, itemId);
        if(itemOld.getUserId().equals(userId)) {
            Item updateItem = ItemMapper.mapToUdateItem(item, itemOld);
            return itemRepository.updateItem(itemId, updateItem);
        } else {
            throw new ValidationException("Изменить поля Item может только владелиц Item");
        }
    }

    @Override
    public Collection<Item> searchItem(Long userId, String text) {
        log.info("Получен запрос от пользователя c id - {} на поиск item в соответствии с - {}", userId, text);
        userService.findUserById(userId);
        if(text.isBlank()) {
            return new ArrayList<>();
        } else {
            return itemRepository.searchItem(text);
        }
    }

}
