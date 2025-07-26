package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NewItemDto {

    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    private Boolean available;
}
