package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.interfacemarker.Create;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class BookingDto {


    private long id;

    @NotNull(groups = {Create.class})
    private LocalDateTime start;

    @NotNull(groups = {Create.class})
    private LocalDateTime end;

    private Item item;

    private User booker;

    private Status status;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ToString.Exclude
    @Positive(groups = {Create.class})
    private Long itemId;
}
