package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    ItemDto toItemDto(Item item);

    @Mapping(source = "user", target = "user")
    @Mapping(source = "itemDto.id", target = "id")
    @Mapping(source = "itemDto.name", target = "name")
    Item toItem(ItemDto itemDto, User user);

    default Item toItemFromItemUpdate(ItemDto itemDto, Item item) {
        return Item.builder()
                .id(item.getId())
                .name(itemDto.getName() != null && !itemDto.getName().isBlank() ? itemDto.getName() : item.getName())
                .description(itemDto.getDescription() != null && !itemDto.getDescription().isBlank() ?
                        itemDto.getDescription() : item.getDescription())
                .available(itemDto.getAvailable() != null ? itemDto.getAvailable() : item.getAvailable())
                .user(item.getUser())
                .build();
    }
}
