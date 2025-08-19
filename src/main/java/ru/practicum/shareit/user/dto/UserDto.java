package ru.practicum.shareit.user.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
@Builder
public class UserDto {
    private Long id;
    private String email;
    private String name;
}
