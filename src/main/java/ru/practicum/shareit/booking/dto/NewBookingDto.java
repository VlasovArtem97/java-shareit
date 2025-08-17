package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

@Data
public class NewBookingDto {
    private LocalDateTime start;

    private LocalDateTime end;

    private Long itemId;
}
