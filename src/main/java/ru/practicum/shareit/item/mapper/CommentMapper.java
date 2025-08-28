package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(source = "user", target = "user")
    @Mapping(source = "item", target = "item")
    @Mapping(source = "commentDto.id", target = "id")
    Comment toComment(CommentDto commentDto, User user, Item item);

    @Mapping(source = "comment.user.name", target = "authorName")
    CommentDto toCommentDto(Comment comment);
}
