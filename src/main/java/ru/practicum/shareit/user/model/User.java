package ru.practicum.shareit.user.model;

/**
 * TODO Sprint add-controllers.
 */
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id")
public class User {
    private Long id;
    private String email;
    private String name;
}
