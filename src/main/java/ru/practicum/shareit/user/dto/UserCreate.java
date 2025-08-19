package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UserCreate {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String name;
}
