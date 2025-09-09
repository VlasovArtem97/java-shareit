package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import ru.practicum.shareit.interfacemarker.Create;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class CommentDto {

    private Long id;
    @NotBlank(groups = Create.class)
    private String text;
    private String authorName;
    private LocalDateTime created = ZonedDateTime.now(ZoneOffset.ofHours(3)).toLocalDateTime();
}
