package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemDtoTest {

    private final JacksonTester<ItemDto> json;
    private final LocalDateTime localDateTime = LocalDateTime.now();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @Test
    void testItemDto() throws Exception {

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("item")
                .lastBooking(localDateTime.minusMinutes(5))
                .nextBooking(null)
                .description("description")
                .comments(List.of(new CommentDto(1L, "comment", "john", localDateTime.minusMinutes(3))))
                .available(true)
                .requestId(1L)
                .build();

        JsonContent<ItemDto> jsonContent = json.write(itemDto);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo("item");
        assertThat(jsonContent).extractingJsonPathStringValue("$.lastBooking").isEqualTo(localDateTime.minusMinutes(5).format(formatter));
        assertThat(jsonContent).extractingJsonPathStringValue("$.nextBooking").isEqualTo(null);
        assertThat(jsonContent).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(jsonContent).extractingJsonPathArrayValue("$.comments").hasSize(1);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("comment");
        assertThat(jsonContent).extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo("john");
        assertThat(jsonContent).extractingJsonPathStringValue("$.comments[0].created").isEqualTo(localDateTime.minusMinutes(3).format(formatter));
    }
}