package ru.practicum.shareit.item.dto;

import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UpdateItemDto {

    private String name;
    private String description;
    private Boolean available;
}
