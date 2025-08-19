package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class NewCommentDto {

    @NotBlank
    private String text;

    private LocalDateTime create = LocalDateTime.now();
}
