package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private final ObjectMapper mapper = JsonMapper.builder()
            .findAndAddModules()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();

    private MockMvc mvc;

    private static final String USER_ID = "X-Sharer-User-Id";

    private BookingDto bookingDtoOne;

    private BookingDto bookingDtoTwo;

    private List<BookingDto> bookingDtoList;

    private User userOne;

    private User userTwo;

    private User userThree;

    private User userFour;

    private Item itemOne;

    private Item itemTwo;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();

        userOne = new User(1L, "john@yandex.ru", "john");

        userTwo = new User(2L, "smith@yandex.ru", "smith");

        userThree = new User(3L, "alex@yandex.ru", "alex");

        userFour = new User(4L, "wolt@yandex.ru", "wolt");

        itemOne = Item.builder()
                .id(1L)
                .user(userOne)
                .available(true)
                .description("One item description")
                .name("One item")
                .build();

        itemTwo = Item.builder()
                .id(2L)
                .user(userTwo)
                .available(true)
                .description("Two item description")
                .name("Two item")
                .build();

        bookingDtoOne = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusMinutes(10))
                .booker(userThree)
                .status(Status.WAITING)
                .item(itemOne)
                .build();

        bookingDtoTwo = BookingDto.builder()
                .itemId(2L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusMinutes(20))
                .booker(userFour)
                .status(Status.WAITING)
                .item(itemTwo)
                .build();

        bookingDtoList = List.of(bookingDtoOne, bookingDtoTwo);
    }

    @Test
    void addBooking() throws Exception {
        when(bookingService.addBooking(anyLong(), any()))
                .thenReturn(bookingDtoOne);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoOne))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOne.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(Status.WAITING.toString())))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoOne.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(bookingDtoOne.getBooker().getName())))
                .andExpect(jsonPath("$.booker.email", is(bookingDtoOne.getBooker().getEmail())))
                .andExpect(jsonPath("$.start", is(bookingDtoOne.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingDtoOne.getEnd().format(formatter))))
                .andExpect(jsonPath("$.item.id", is(bookingDtoOne.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDtoOne.getItem().getName())))
                .andExpect(jsonPath("$.item.description", is(bookingDtoOne.getItem().getDescription())))
                .andExpect(jsonPath("$.item.user.id", is(bookingDtoOne.getItem().getUser().getId()),
                        Long.class))
                .andExpect(jsonPath("$.item.user.name", is(bookingDtoOne.getItem().getUser().getName())))
                .andExpect(jsonPath("$.item.user.email", is(bookingDtoOne.getItem().getUser().getEmail())));
    }

    @Test
    void updateBookingStatus() throws Exception {
        when(bookingService.updateBookingStatus(anyLong(), anyLong(), anyString()))
                .thenReturn(bookingDtoOne);
        bookingDtoOne.setStatus(Status.APPROVED);

        mvc.perform(patch("/bookings/{bookingId}?approved={approved}", 1, true)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOne.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(Status.APPROVED.toString())))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoOne.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(bookingDtoOne.getBooker().getName())))
                .andExpect(jsonPath("$.booker.email", is(bookingDtoOne.getBooker().getEmail())))
                .andExpect(jsonPath("$.start", is(bookingDtoOne.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingDtoOne.getEnd().format(formatter))))
                .andExpect(jsonPath("$.item.id", is(bookingDtoOne.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDtoOne.getItem().getName())))
                .andExpect(jsonPath("$.item.description", is(bookingDtoOne.getItem().getDescription())))
                .andExpect(jsonPath("$.item.user.id", is(bookingDtoOne.getItem().getUser().getId()),
                        Long.class))
                .andExpect(jsonPath("$.item.user.name", is(bookingDtoOne.getItem().getUser().getName())))
                .andExpect(jsonPath("$.item.user.email", is(bookingDtoOne.getItem().getUser().getEmail())));
    }

    @Test
    void findBookingStatus() throws Exception {
        when(bookingService.findBookingStatus(anyLong(), anyLong()))
                .thenReturn(bookingDtoOne);

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOne.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(Status.WAITING.toString())))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoOne.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(bookingDtoOne.getBooker().getName())))
                .andExpect(jsonPath("$.booker.email", is(bookingDtoOne.getBooker().getEmail())))
                .andExpect(jsonPath("$.start", is(bookingDtoOne.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingDtoOne.getEnd().format(formatter))))
                .andExpect(jsonPath("$.item.id", is(bookingDtoOne.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDtoOne.getItem().getName())))
                .andExpect(jsonPath("$.item.description", is(bookingDtoOne.getItem().getDescription())))
                .andExpect(jsonPath("$.item.user.id", is(bookingDtoOne.getItem().getUser().getId()),
                        Long.class))
                .andExpect(jsonPath("$.item.user.name", is(bookingDtoOne.getItem().getUser().getName())))
                .andExpect(jsonPath("$.item.user.email", is(bookingDtoOne.getItem().getUser().getEmail())));
    }

    @Test
    void findBookingUser() throws Exception {
        when(bookingService.findBookingUser(anyLong(), anyString()))
                .thenReturn(bookingDtoList);

        mvc.perform(get("/bookings?state={state}", "all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDtoList)))
                .andExpect(jsonPath("$.length()").value(bookingDtoList.size()));
    }

    @Test
    void findBookingOwner() throws Exception {
        when(bookingController.findBookingOwner(anyLong(), anyString()))
                .thenReturn(bookingDtoList);

        mvc.perform(get("/bookings/owner?state={state}", "all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDtoList)))
                .andExpect(jsonPath("$.length()").value(bookingDtoList.size()));
    }
}