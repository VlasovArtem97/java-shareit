package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {

    private Long id;
    private Long userId;
    private String name;
    private String description;
    private Boolean available;
}
