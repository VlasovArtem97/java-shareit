package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemOwnerDto {

    private String name;
    private String description;
}
