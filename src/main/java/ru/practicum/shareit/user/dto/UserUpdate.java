package ru.practicum.shareit.user.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UserUpdate {
    private String email;
    private String name;
}
