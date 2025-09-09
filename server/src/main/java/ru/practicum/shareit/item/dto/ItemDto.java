package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import ru.practicum.shareit.interfacemarker.Create;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ItemDto {

    private Long id;

    @NotBlank(groups = Create.class)
    private String name;

    @NotBlank(groups = Create.class)
    private String description;

    @NotNull(groups = Create.class)
    private Boolean available;

    private LocalDateTime lastBooking;

    private LocalDateTime nextBooking;

    private List<CommentDto> comments;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Positive(groups = {Create.class})
    private Long requestId;

}
