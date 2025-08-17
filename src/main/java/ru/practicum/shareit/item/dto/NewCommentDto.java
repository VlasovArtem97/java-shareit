package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class NewCommentDto {

    @NotBlank
    private String text;

    private LocalDateTime create = LocalDateTime.now();
}
