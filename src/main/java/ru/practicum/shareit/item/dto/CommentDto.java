package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class CommentDto {

    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
