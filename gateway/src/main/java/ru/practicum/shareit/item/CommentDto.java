package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.interfacemarker.Create;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    @NotBlank(groups = Create.class)
    private String text;
}
