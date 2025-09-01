package ru.practicum.shareit.request.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.shareit.interfacemarker.Create;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
@Builder
public class ItemRequestDto {

    private Long id;

    @NotNull(groups = {Create.class})
    private String description;

    private LocalDateTime created = LocalDateTime.now();

    private Long userId;

    private List<ItemDto> items;
}
