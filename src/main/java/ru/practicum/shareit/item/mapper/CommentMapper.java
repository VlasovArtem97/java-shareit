package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.NewCommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class CommentMapper {

    public static Comment mapToCommentFromNewCommentDto(User user, Item item, NewCommentDto newCommentDto) {
        return Comment.builder()
                .text(newCommentDto.getText())
                .user(user)
                .item(item)
                .create(newCommentDto.getCreate())
                .build();
    }

    public static CommentDto mapToCommentDtoFromComment(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getUser().getName())
                .item(comment.getItem())
                .created(comment.getCreate())
                .build();
    }
}
