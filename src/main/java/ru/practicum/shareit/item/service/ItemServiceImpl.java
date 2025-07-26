package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
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
    public ItemDto getItemById(Long userId, Long itemId) {
        log.info("Получен запрос на получение Item по Id - {} от пользователя с id - {}", itemId, userId);
        userService.findUserById(userId);
        Item item = itemRepository.getItemById(itemId).orElseThrow(() -> new NotFoundException("Item c ID - " + itemId +
                " не найден"));
        ItemDto itemDto = ItemMapper.mapToItemDto(item);
        log.debug("Полученный предмет: {}", itemDto);
        return itemDto;
    }

    @Override
    public Collection<ItemOwnerDto> getItemOwner(Long userId) {
        log.info("Получен запрос на получения списка Item пользователя с id - {}", userId);
        userService.findUserById(userId);
        Collection<ItemOwnerDto> itemOwnerDtos = itemRepository.getItemOwner(userId).stream()
                .map(ItemMapper::mapToItemOwnerDto)
                .toList();
        log.debug("Список Item: {} пользователя с id - {}", itemOwnerDtos, userId);
        return itemOwnerDtos;
    }

    @Override
    public ItemDto addNewItem(Long userId, NewItemDto item) {
        log.info("Получен запрос на добавление нового Item: {}, пользователем с id - {}", item, userId);
        userService.findUserById(userId);
        Item itemNew = ItemMapper.mapToItem(item);
        Item itemCreate = itemRepository.addNewItem(userId, itemNew);
        ItemDto addedItem = ItemMapper.mapToItemDto(itemCreate);
        log.debug("Добавленный Item: {}", addedItem);
        return addedItem;
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, UpdateItemDto item) {
        log.info("Получен запрос на обновление данных Item: {}", item);
        Item itemOld = ItemMapper.mapToItemFromItemDto(getItemById(userId, itemId));
        if (itemOld.getUserId().equals(userId)) {
            Item updateItem = ItemMapper.mapToUpdateItem(item, itemOld);
            Item updatedItem = itemRepository.updateItem(itemId, updateItem);
            ItemDto mapToItemDto = ItemMapper.mapToItemDto(updatedItem);
            log.debug("Обновленный Item: {}", mapToItemDto);
            return mapToItemDto;
        } else {
            throw new ValidationException("Изменить поля Item может только владелиц Item");
        }
    }

    @Override
    public Collection<ItemDto> searchItem(Long userId, String text) {
        log.info("Получен запрос от пользователя c id - {} на поиск item в соответствии с - {}", userId, text);
        userService.findUserById(userId);
        if (text.isBlank()) {
            return new ArrayList<>();
        } else {
            Collection<ItemDto> itemsDto = itemRepository.searchItem(text).stream()
                    .map(ItemMapper::mapToItemDto)
                    .toList();
            log.debug("Список Item: {}", itemsDto);
            return itemsDto;
        }
    }

}
