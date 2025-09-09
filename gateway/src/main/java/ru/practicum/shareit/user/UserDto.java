package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.interfacemarker.Create;
import ru.practicum.shareit.interfacemarker.Update;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @NotBlank(groups = {Create.class})
    @Email(groups = {Update.class, Create.class})
    private String email;
    @NotBlank(groups = {Create.class})
    private String name;
}
