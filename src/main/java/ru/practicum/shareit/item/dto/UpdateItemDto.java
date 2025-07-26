package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateItemDto {

    @NotNull
    private String name;
    @NotNull
    private String description;
    private Boolean available;
}
