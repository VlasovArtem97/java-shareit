package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.validatelocaldatetime.DateTimeValidate;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@DateTimeValidate
public class BookItemRequestDto {

    @NotNull
    @Positive
    private long itemId;
    @FutureOrPresent
//    @JsonDeserialize(using = DeserializeLocalDateTime.class)
    private LocalDateTime start;
    @Future
//    @JsonDeserialize(using = DeserializeLocalDateTime.class)
    private LocalDateTime end;
}
