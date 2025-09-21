package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingDtoTest {

    private final JacksonTester<BookingDto> json;
    private final LocalDateTime localDateTime = LocalDateTime.now();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @Test
    void testBookingDto() throws Exception {

        User userOne = new User(1L, "alex@yandex.ru", "alex");
        User userTwo = new User(2L, "john@yandex.ru", "john");
        User userThree = new User(3L, "smith@yandex.ru", "smith");
        ItemRequest itemRequest = new ItemRequest(1L, "request", localDateTime, userTwo, null);
        Item item = new Item(1L, userOne, "itemName", "itemDescription", true, itemRequest);

        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(localDateTime)
                .end(localDateTime.plusMinutes(10))
                .itemId(1L)
                .booker(userThree)
                .status(Status.WAITING)
                .item(item)
                .build();

        JsonContent<BookingDto> jsonContent = json.write(bookingDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.start")
                .isEqualTo(localDateTime.format(formatter));
        assertThat(jsonContent).extractingJsonPathStringValue("$.end")
                .isEqualTo(localDateTime.plusMinutes(10).format(formatter));
        assertThat(jsonContent).extractingJsonPathNumberValue("$.booker.id")
                .isEqualTo(3);
        assertThat(jsonContent).extractingJsonPathStringValue("$.booker.name")
                .isEqualTo("smith");
        assertThat(jsonContent).extractingJsonPathStringValue("$.booker.email")
                .isEqualTo("smith@yandex.ru");
        assertThat(jsonContent).extractingJsonPathStringValue("$.status")
                .isEqualTo(Status.WAITING.toString());
        assertThat(jsonContent).extractingJsonPathNumberValue("$.item.id")
                .isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.item.user.id")
                .isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.item.user.name")
                .isEqualTo("alex");
        assertThat(jsonContent).extractingJsonPathStringValue("$.item.user.email")
                .isEqualTo("alex@yandex.ru");
        assertThat(jsonContent).extractingJsonPathStringValue("$.item.name")
                .isEqualTo("itemName");
        assertThat(jsonContent).extractingJsonPathStringValue("$.item.description")
                .isEqualTo("itemDescription");
        assertThat(jsonContent).extractingJsonPathBooleanValue("$.item.available")
                .isEqualTo(true);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.item.itemRequest.id")
                .isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.item.itemRequest.description")
                .isEqualTo("request");
        assertThat(jsonContent).extractingJsonPathStringValue("$.item.itemRequest.created")
                .isEqualTo(localDateTime.format(formatter));
        assertThat(jsonContent).extractingJsonPathNumberValue("$.item.itemRequest.user.id")
                .isEqualTo(2);
        assertThat(jsonContent).extractingJsonPathStringValue("$.item.itemRequest.user.name")
                .isEqualTo("john");
        assertThat(jsonContent).extractingJsonPathStringValue("$.item.itemRequest.user.email")
                .isEqualTo("john@yandex.ru");
        assertThat(jsonContent).extractingJsonPathArrayValue("$.item.itemRequest.item").isNull();
    }
}