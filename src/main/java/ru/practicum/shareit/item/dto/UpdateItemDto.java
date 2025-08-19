package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UpdateItemDto {

    private String name;
    private String description;
    private Boolean available;
}
