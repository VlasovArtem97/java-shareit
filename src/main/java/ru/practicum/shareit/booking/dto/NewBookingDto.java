package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class NewBookingDto {

    private LocalDateTime start;

    private LocalDateTime end;

    private Long itemId;
}
