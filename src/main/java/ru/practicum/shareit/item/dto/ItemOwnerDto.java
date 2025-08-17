package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemOwnerDto {

    private Long Id;
    private String name;
    private String description;
    private Boolean available;
    private LocalDateTime endTimeUseItem;
    private LocalDateTime nextTimeUseItem;
    private List<CommentDto> commentDtoList;
}
