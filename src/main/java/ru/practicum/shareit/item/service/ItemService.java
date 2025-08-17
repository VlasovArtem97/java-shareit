package ru.practicum.shareit.item.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemService {

    ItemDto getItemById(Long userId, Long itemId);

    Collection<ItemOwnerDto> getItemOwner(Long userId);

    ItemDto addNewItem(Long userId, NewItemDto item);

    ItemDto updateItem(Long userId, Long itemId, UpdateItemDto item);

    Collection<ItemDto> searchItem(Long userId, String text);

    Item returnFindItemById(Long itemId);

    List<Item> getItemByUserId(Long userId);

    CommentDto addComment(Long userId, Long itemId, NewCommentDto newCommentDto);
}
