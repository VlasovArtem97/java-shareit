package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemSearch;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ItemService {


    ItemDto addNewItem(Long userId, ItemDto item);

    ItemDto updateItem(Long userId, Long itemId, ItemDto item);

    Collection<ItemSearch> searchItem(Long userId, String text);

    Item returnFindItemById(Long itemId);

    List<Item> getItemByUserId(Long userId);

    CommentDto addComment(User user, Item item, CommentDto newCommentDto);

    Map<Long, List<Comment>> findAllCommentsItems(List<Long> itemIds);

}
