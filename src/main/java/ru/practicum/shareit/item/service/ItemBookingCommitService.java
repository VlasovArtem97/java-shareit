package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewCommentDto;

import java.util.List;

public interface ItemBookingCommitService {

    ItemDto getItemById(Long userId, Long itemId);

    List<ItemDto> getItemOwnerById(Long userId);

    CommentDto addComment(Long userId, Long itemId, NewCommentDto newCommentDto);


}
