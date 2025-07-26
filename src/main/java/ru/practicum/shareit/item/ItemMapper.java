package ru.practicum.shareit.item;

import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    public static Item mapToItem(NewItemDto itemDto) {
        Item item = new Item();
        if(itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        } else {
            throw new ValidationException("Наименование item должен быть указан");
        }
        if(itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        } else {
            throw new ValidationException("Описание item должен быть указан");
        }
        if(itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        } else {
         throw new ValidationException("Статус item должен быть указан");
        }
        return item;
    }

    public static Item mapToUdateItem(UpdateItemDto updateItemDto, Item item) {
        if(updateItemDto.getName() != null && !updateItemDto.getName().isBlank()) {
            item.setName(updateItemDto.getName());
        }
        if(updateItemDto.getDescription() != null && !updateItemDto.getDescription().isBlank()) {
            item.setDescription(updateItemDto.getDescription());
        }
        if(updateItemDto.getAvailable() != null) {
            item.setAvailable(updateItemDto.getAvailable());
        }
        return item;
    }

    public static ItemOwnerDto mapToItemOwnerDto(Item item) {
        ItemOwnerDto itemOwnerDto = new ItemOwnerDto();
        itemOwnerDto.setName(item.getName());
        itemOwnerDto.setDescription(item.getDescription());
        return itemOwnerDto;
    }
}
