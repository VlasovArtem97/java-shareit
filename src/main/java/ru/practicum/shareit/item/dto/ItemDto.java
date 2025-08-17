package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
public class ItemDto {

    private Long id;
    private String name;
    private String description;
    private User user;
    private Boolean available;
    private LocalDateTime lastBooking;
    private LocalDateTime nextBooking;
    private List<CommentDto> commentDtos;

    public ItemDto(Long id, String name, String description, User user, Boolean available, List<CommentDto> commentDtos) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.user = user;
        this.available = available;
        this.commentDtos = commentDtos;
    }

    public ItemDto(Long id, String name, String description, User user, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.user = user;
        this.available = available;
    }

    public ItemDto(Long id, String name, String description, User user, Boolean available, LocalDateTime lastBooking,
                   LocalDateTime nextBooking, List<CommentDto> commentDtos) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.user = user;
        this.available = available;
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
        this.commentDtos = commentDtos;
    }
}
