package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {

    @Mapping(source = "itemRequestDto.id", target = "id")
    @Mapping(source = "user", target = "user")
    ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "item", target = "items")
    ItemRequestDto toItemRequestDto(ItemRequest itemRequest);
}
