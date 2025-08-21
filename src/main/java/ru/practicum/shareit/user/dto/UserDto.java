package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import ru.practicum.shareit.interfacemarker.Create;
import ru.practicum.shareit.interfacemarker.Update;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
@Builder
public class UserDto {

    private Long id;
    @NotBlank(groups = {Create.class})
    @Email(groups = {Update.class, Create.class})
    private String email;
    @NotBlank(groups = {Create.class})
    private String name;
}
