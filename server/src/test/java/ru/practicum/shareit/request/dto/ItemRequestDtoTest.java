package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestDtoTest {

    private final JacksonTester<ItemRequestDto> json;
    private final LocalDateTime localDateTime = LocalDateTime.now();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @Test
    void testItemRequestDto() throws Exception {

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .created(localDateTime)
                .userId(1L)
                .items(List.of(new ItemDto(1L, "item", "description", true,
                        null, null, null, 1L)))
                .build();

        JsonContent<ItemRequestDto> jsonContent = json.write(itemRequestDto);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(jsonContent).extractingJsonPathStringValue("$.created").isEqualTo(localDateTime.format(formatter));
        assertThat(jsonContent).extractingJsonPathNumberValue("$.userId").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathArrayValue("$.items").hasSize(1);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.items[0].description").isEqualTo("description");
        assertThat(jsonContent).extractingJsonPathBooleanValue("$.items[0].available").isEqualTo(true);
        assertThat(jsonContent).extractingJsonPathValue("$.items[0].lastBooking").isNull();
        assertThat(jsonContent).extractingJsonPathValue("$.items[0].nextBooking").isNull();
        assertThat(jsonContent).extractingJsonPathValue("$.comments").isNull();
    }

}