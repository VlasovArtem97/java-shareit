package ru.practicum.shareit.booking.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class BookingServiceImplTest {

    private final EntityManager entityManager;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private BookingDto bookingDtoOne;

    private BookingDto bookingDtoTwo;

    private List<BookingDto> bookingDtoList;

    private User userOne;

    private User userTwo;

    private User userThree;

    private User userFour;

    private ItemDto itemDtoOne;

    private ItemDto itemDtoTwo;

    private Item itemOne;

    private Item itemTwo;


    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        userOne = userRepository.save(new User(null, "john@yandex.ru", "john"));

        userTwo = userRepository.save(new User(null, "smith@yandex.ru", "smith"));

        userThree = userRepository.save(new User(null, "alex@yandex.ru", "alex"));

        userFour = userRepository.save(new User(null, "wolt@yandex.ru", "wolt"));

        itemDtoOne = ItemDto.builder()
                .id(null)
                .available(true)
                .description("One item description")
                .name("One item")
                .build();

        itemDtoTwo = ItemDto.builder()
                .id(null)
                .available(true)
                .description("Two item description")
                .name("Two item")
                .build();

        itemOne = itemRepository.save(itemMapper.toItem(itemDtoOne, userOne));
        itemTwo = itemRepository.save(itemMapper.toItem(itemDtoTwo, userTwo));


        bookingDtoOne = BookingDto.builder()
                .id(null)
                .start(now)
                .end(LocalDateTime.now().plusMinutes(10))
                .itemId(itemOne.getId())
                .build();

        bookingDtoTwo = BookingDto.builder()
                .id(null)
                .start(now)
                .end(LocalDateTime.now().plusMinutes(20))
                .itemId(itemTwo.getId())
                .build();

        bookingDtoList = List.of(bookingDtoOne, bookingDtoTwo);
    }

    @Test
    void addBooking() {
        BookingDto booking = bookingService.addBooking(userThree.getId(), bookingDtoOne);

        TypedQuery<Booking> typedQuery = entityManager.createQuery("Select b FROM Booking b WHERE b.item.id = :itemId",
                Booking.class);
        Booking booking1 = typedQuery.setParameter("itemId", bookingDtoOne.getItemId()).getSingleResult();

        assertThat("Проверка равенства id", booking1.getId(), equalTo(booking.getId()));
        assertThat("Проверка равенства start", booking1.getStart(), equalTo(booking.getStart()));
        assertThat("Проверка равенства end", booking1.getEnd(), equalTo(booking.getEnd()));
        assertThat("Проверка равенства id item", booking1.getItem().getId(), equalTo(itemOne.getId()));
        assertThat("Проверка равенства name item", booking1.getItem().getName(), equalTo(itemOne.getName()));
        assertThat("Проверка равенства description item", booking1.getItem().getDescription(),
                equalTo(itemOne.getDescription()));
        assertThat("Проверка равенства available item", booking1.getItem().getAvailable(),
                equalTo(itemOne.getAvailable()));
        assertThat("Проверка равенства id booker", booking1.getBooker().getId(),
                equalTo(userThree.getId()));
        assertThat("Проверка равенства name booker", booking1.getBooker().getName(),
                equalTo(userThree.getName()));
        assertThat("Проверка равенства email booker", booking1.getBooker().getEmail(),
                equalTo(userThree.getEmail()));
        assertThat("Проверка равенства status", booking1.getStatus(), equalTo(Status.WAITING));

        itemOne.setAvailable(false);
        assertThrows(IllegalStateException.class, () -> bookingService.addBooking(userThree.getId(), bookingDtoOne),
                "Должно выброситься исключение при условии, что item недоступен для брони");
    }

    @Test
    void updateBookingStatus() {
        Booking booking = bookingRepository.save(bookingMapper.toBooking(bookingDtoOne, userThree, itemOne));
        BookingDto bookingDto = bookingService.updateBookingStatus(userOne.getId(), booking.getId(), "true");

        assertThat("Проверка равенства id", bookingDto.getId(), equalTo(booking.getId()));
        assertThat("Проверка равенства start", bookingDto.getStart(), equalTo(booking.getStart()));
        assertThat("Проверка равенства end", bookingDto.getEnd(), equalTo(booking.getEnd()));
        assertThat("Проверка равенства id item", bookingDto.getItem().getId(), equalTo(itemOne.getId()));
        assertThat("Проверка равенства name item", bookingDto.getItem().getName(), equalTo(itemOne.getName()));
        assertThat("Проверка равенства description item", bookingDto.getItem().getDescription(),
                equalTo(itemOne.getDescription()));
        assertThat("Проверка равенства available item", bookingDto.getItem().getAvailable(),
                equalTo(itemOne.getAvailable()));
        assertThat("Проверка равенства id booker", bookingDto.getBooker().getId(),
                equalTo(userThree.getId()));
        assertThat("Проверка равенства name booker", bookingDto.getBooker().getName(),
                equalTo(userThree.getName()));
        assertThat("Проверка равенства email booker", bookingDto.getBooker().getEmail(),
                equalTo(userThree.getEmail()));
        assertThat("Проверка равенства status", bookingDto.getStatus(), equalTo(Status.APPROVED));

        assertThrows(IllegalStateException.class, () -> bookingService.updateBookingStatus(userThree.getId(),
                        booking.getId(), "true"),
                "Должно выброситься исключение при условии, что это не владелец item");

        BookingDto bookingDto2 = bookingService.updateBookingStatus(userOne.getId(), booking.getId(), "false");

        assertThat("Проверка равенства id", bookingDto2.getId(), equalTo(booking.getId()));
        assertThat("Проверка равенства start", bookingDto2.getStart(), equalTo(booking.getStart()));
        assertThat("Проверка равенства end", bookingDto2.getEnd(), equalTo(booking.getEnd()));
        assertThat("Проверка равенства id item", bookingDto2.getItem().getId(), equalTo(itemOne.getId()));
        assertThat("Проверка равенства name item", bookingDto2.getItem().getName(), equalTo(itemOne.getName()));
        assertThat("Проверка равенства description item", bookingDto2.getItem().getDescription(),
                equalTo(itemOne.getDescription()));
        assertThat("Проверка равенства available item", bookingDto2.getItem().getAvailable(),
                equalTo(itemOne.getAvailable()));
        assertThat("Проверка равенства id booker", bookingDto2.getBooker().getId(),
                equalTo(userThree.getId()));
        assertThat("Проверка равенства name booker", bookingDto2.getBooker().getName(),
                equalTo(userThree.getName()));
        assertThat("Проверка равенства email booker", bookingDto2.getBooker().getEmail(),
                equalTo(userThree.getEmail()));
        assertThat("Проверка равенства status", bookingDto2.getStatus(), equalTo(Status.REJECTED));
    }

    @Test
    void findBookingStatus() {
        Booking booking = bookingRepository.save(bookingMapper.toBooking(bookingDtoOne, userThree, itemOne));

        BookingDto bookingDto = bookingService.findBookingStatus(userThree.getId(), booking.getId());
        assertThat("Проверка равенства id", bookingDto.getId(), equalTo(booking.getId()));
        assertThat("Проверка равенства start", bookingDto.getStart(), equalTo(booking.getStart()));
        assertThat("Проверка равенства end", bookingDto.getEnd(), equalTo(booking.getEnd()));
        assertThat("Проверка равенства id item", bookingDto.getItem().getId(), equalTo(itemOne.getId()));
        assertThat("Проверка равенства name item", bookingDto.getItem().getName(), equalTo(itemOne.getName()));
        assertThat("Проверка равенства description item", bookingDto.getItem().getDescription(),
                equalTo(itemOne.getDescription()));
        assertThat("Проверка равенства available item", bookingDto.getItem().getAvailable(),
                equalTo(itemOne.getAvailable()));
        assertThat("Проверка равенства id booker", bookingDto.getBooker().getId(),
                equalTo(userThree.getId()));
        assertThat("Проверка равенства name booker", bookingDto.getBooker().getName(),
                equalTo(userThree.getName()));
        assertThat("Проверка равенства email booker", bookingDto.getBooker().getEmail(),
                equalTo(userThree.getEmail()));
        assertThat("Проверка равенства status", bookingDto.getStatus(), equalTo(Status.WAITING));

        assertThrows(ConflictException.class, () -> bookingService.findBookingStatus(userFour.getId(), booking.getId()),
                "Должно выброситься исключение при условии, что информацию о бронировании запрашивает " +
                        "сторонний пользователь");
    }

    @Test
    void findBookingUser() {
        Booking booking = bookingRepository.save(bookingMapper.toBooking(bookingDtoOne, userThree, itemOne));
        List<BookingDto> bookingDtos = bookingService.findBookingUser(userThree.getId(), "all");

        assertThat("Проверка списка на null", bookingDtos, notNullValue());
        assertThat("Проверка размера списка", bookingDtos.size(), equalTo(1));
        assertThat("Проверка равенства id", bookingDtos.getFirst().getId(), equalTo(booking.getId()));
        assertThat("Проверка равенства start", bookingDtos.getFirst().getStart(), equalTo(booking.getStart()));
        assertThat("Проверка равенства end", bookingDtos.getFirst().getEnd(), equalTo(booking.getEnd()));
        assertThat("Проверка равенства id item", bookingDtos.getFirst().getItem().getId(), equalTo(itemOne.getId()));
        assertThat("Проверка равенства name item", bookingDtos.getFirst().getItem().getName(), equalTo(itemOne.getName()));
        assertThat("Проверка равенства description item", bookingDtos.getFirst().getItem().getDescription(),
                equalTo(itemOne.getDescription()));
        assertThat("Проверка равенства available item", bookingDtos.getFirst().getItem().getAvailable(),
                equalTo(itemOne.getAvailable()));
        assertThat("Проверка равенства id booker", bookingDtos.getFirst().getBooker().getId(),
                equalTo(userThree.getId()));
        assertThat("Проверка равенства name booker", bookingDtos.getFirst().getBooker().getName(),
                equalTo(userThree.getName()));
        assertThat("Проверка равенства email booker", bookingDtos.getFirst().getBooker().getEmail(),
                equalTo(userThree.getEmail()));
        assertThat("Проверка равенства status", bookingDtos.getFirst().getStatus(), equalTo(Status.WAITING));

        booking.setStatus(Status.APPROVED);
        booking.setStart(LocalDateTime.now().minusMinutes(10));
        booking.setEnd(LocalDateTime.now().plusMinutes(5));
        List<BookingDto> bookingDtos2 = bookingService.findBookingUser(userThree.getId(), "current");
        assertThat("Проверка списка на null", bookingDtos2, notNullValue());
        assertThat("Проверка размера списка", bookingDtos2.size(), equalTo(1));
        assertThat("Проверка равенства status", bookingDtos2.getFirst().getStatus(), equalTo(Status.APPROVED));

        booking.setStatus(Status.APPROVED);
        booking.setStart(LocalDateTime.now().minusMinutes(15));
        booking.setEnd(LocalDateTime.now().minusMinutes(10));
        List<BookingDto> bookingDtos3 = bookingService.findBookingUser(userThree.getId(), "past");
        assertThat("Проверка списка на null", bookingDtos3, notNullValue());
        assertThat("Проверка размера списка", bookingDtos3.size(), equalTo(1));
        assertThat("Проверка равенства status", bookingDtos3.getFirst().getStatus(), equalTo(Status.APPROVED));

        booking.setStatus(Status.APPROVED);
        booking.setStart(LocalDateTime.now().plusMinutes(10));
        booking.setEnd(LocalDateTime.now().plusMinutes(15));
        List<BookingDto> bookingDtos4 = bookingService.findBookingUser(userThree.getId(), "future");
        assertThat("Проверка списка на null", bookingDtos4, notNullValue());
        assertThat("Проверка размера списка", bookingDtos4.size(), equalTo(1));
        assertThat("Проверка равенства status", bookingDtos4.getFirst().getStatus(), equalTo(Status.APPROVED));

        booking.setStatus(Status.WAITING);
        booking.setStart(LocalDateTime.now().plusMinutes(10));
        booking.setEnd(LocalDateTime.now().plusMinutes(15));
        List<BookingDto> bookingDtos5 = bookingService.findBookingUser(userThree.getId(), "waiting");
        assertThat("Проверка списка на null", bookingDtos5, notNullValue());
        assertThat("Проверка размера списка", bookingDtos5.size(), equalTo(1));
        assertThat("Проверка равенства status", bookingDtos5.getFirst().getStatus(), equalTo(Status.WAITING));

        booking.setStatus(Status.REJECTED);
        List<BookingDto> bookingDtos6 = bookingService.findBookingUser(userThree.getId(), "rejected");
        assertThat("Проверка списка на null", bookingDtos6, notNullValue());
        assertThat("Проверка размера списка", bookingDtos6.size(), equalTo(1));
        assertThat("Проверка равенства status", bookingDtos6.getFirst().getStatus(), equalTo(Status.REJECTED));

        booking.setStart(LocalDateTime.now().plusMinutes(10));
        booking.setEnd(LocalDateTime.now().plusMinutes(15));
        List<BookingDto> bookingDtos7 = bookingService.findBookingUser(userThree.getId(), "waiting");
        assertThat("Проверка списка на null", bookingDtos7, empty());
    }

    @Test
    void findBookingOwner() {
        Booking booking = bookingRepository.save(bookingMapper.toBooking(bookingDtoOne, userThree, itemOne));
        List<BookingDto> bookingDtos = bookingService.findBookingOwner(userOne.getId(), "all");

        assertThat("Проверка списка на null", bookingDtos, notNullValue());
        assertThat("Проверка размера списка", bookingDtos.size(), equalTo(1));
        assertThat("Проверка равенства id", bookingDtos.getFirst().getId(), equalTo(booking.getId()));
        assertThat("Проверка равенства start", bookingDtos.getFirst().getStart(), equalTo(booking.getStart()));
        assertThat("Проверка равенства end", bookingDtos.getFirst().getEnd(), equalTo(booking.getEnd()));
        assertThat("Проверка равенства id item", bookingDtos.getFirst().getItem().getId(), equalTo(itemOne.getId()));
        assertThat("Проверка равенства name item", bookingDtos.getFirst().getItem().getName(), equalTo(itemOne.getName()));
        assertThat("Проверка равенства description item", bookingDtos.getFirst().getItem().getDescription(),
                equalTo(itemOne.getDescription()));
        assertThat("Проверка равенства available item", bookingDtos.getFirst().getItem().getAvailable(),
                equalTo(itemOne.getAvailable()));
        assertThat("Проверка равенства id booker", bookingDtos.getFirst().getBooker().getId(),
                equalTo(userThree.getId()));
        assertThat("Проверка равенства name booker", bookingDtos.getFirst().getBooker().getName(),
                equalTo(userThree.getName()));
        assertThat("Проверка равенства email booker", bookingDtos.getFirst().getBooker().getEmail(),
                equalTo(userThree.getEmail()));
        assertThat("Проверка равенства status", bookingDtos.getFirst().getStatus(), equalTo(Status.WAITING));

        booking.setStatus(Status.APPROVED);
        booking.setStart(LocalDateTime.now().minusMinutes(10));
        booking.setEnd(LocalDateTime.now().plusMinutes(5));
        List<BookingDto> bookingDtos2 = bookingService.findBookingOwner(userOne.getId(), "current");
        assertThat("Проверка списка на null", bookingDtos2, notNullValue());
        assertThat("Проверка размера списка", bookingDtos2.size(), equalTo(1));
        assertThat("Проверка равенства status", bookingDtos2.getFirst().getStatus(), equalTo(Status.APPROVED));

        booking.setStatus(Status.APPROVED);
        booking.setStart(LocalDateTime.now().minusMinutes(15));
        booking.setEnd(LocalDateTime.now().minusMinutes(10));
        List<BookingDto> bookingDtos3 = bookingService.findBookingOwner(userOne.getId(), "past");
        assertThat("Проверка списка на null", bookingDtos3, notNullValue());
        assertThat("Проверка размера списка", bookingDtos3.size(), equalTo(1));
        assertThat("Проверка равенства status", bookingDtos3.getFirst().getStatus(), equalTo(Status.APPROVED));

        booking.setStatus(Status.APPROVED);
        booking.setStart(LocalDateTime.now().plusMinutes(10));
        booking.setEnd(LocalDateTime.now().plusMinutes(15));
        List<BookingDto> bookingDtos4 = bookingService.findBookingOwner(userOne.getId(), "future");
        assertThat("Проверка списка на null", bookingDtos4, notNullValue());
        assertThat("Проверка размера списка", bookingDtos4.size(), equalTo(1));
        assertThat("Проверка равенства status", bookingDtos4.getFirst().getStatus(), equalTo(Status.APPROVED));

        booking.setStatus(Status.WAITING);
        booking.setStart(LocalDateTime.now().plusMinutes(10));
        booking.setEnd(LocalDateTime.now().plusMinutes(15));
        List<BookingDto> bookingDtos5 = bookingService.findBookingOwner(userOne.getId(), "waiting");
        assertThat("Проверка списка на null", bookingDtos5, notNullValue());
        assertThat("Проверка размера списка", bookingDtos5.size(), equalTo(1));
        assertThat("Проверка равенства status", bookingDtos5.getFirst().getStatus(), equalTo(Status.WAITING));

        booking.setStatus(Status.REJECTED);
        List<BookingDto> bookingDtos6 = bookingService.findBookingOwner(userOne.getId(), "rejected");
        assertThat("Проверка списка на null", bookingDtos6, notNullValue());
        assertThat("Проверка размера списка", bookingDtos6.size(), equalTo(1));
        assertThat("Проверка равенства status", bookingDtos6.getFirst().getStatus(), equalTo(Status.REJECTED));

        booking.setStart(LocalDateTime.now().plusMinutes(10));
        booking.setEnd(LocalDateTime.now().plusMinutes(15));
        List<BookingDto> bookingDtos7 = bookingService.findBookingOwner(userOne.getId(), "waiting");
        assertThat("Проверка списка на null", bookingDtos7, empty());

        assertThrows(IllegalStateException.class,
                () -> bookingService.findBookingOwner(userFour.getId(), "rejected"),
                "Должно выброситься исключение при условии, что у пользователя отсутствуют Item");
    }

    @Test
    void findBookingForComment() {
        Booking booking = bookingRepository.save(bookingMapper.toBooking(bookingDtoOne, userThree, itemOne));
        booking.setStatus(Status.APPROVED);
        booking.setStart(LocalDateTime.now().minusMinutes(15));
        booking.setEnd(LocalDateTime.now().minusMinutes(10));
        List<Booking> bookings = bookingService.findBookingForComment(userThree.getId(), itemOne.getId(),
                LocalDateTime.now());
        assertThat("Проверка списка на null", bookings, not(empty()));
        assertThat("Проверка размера списка", bookings.size(), equalTo(1));

        List<Booking> bookings1 = bookingService.findBookingForComment(userThree.getId(), itemOne.getId(),
                LocalDateTime.now().minusMinutes(20));
        assertThat("Проверка списка на null", bookings1, empty());
    }

    @Test
    void findAllBookingByItemIds() {
        Booking booking = bookingRepository.save(bookingMapper.toBooking(bookingDtoOne, userThree, itemOne));
        Booking booking2 = bookingRepository.save(bookingMapper.toBooking(bookingDtoTwo, userFour, itemTwo));
        Map<Long, Booking> bookingMap1 = bookingService.findAllBookingByItemIds(List.of(itemOne.getId(), itemTwo.getId()),
                LocalDateTime.now().plusMinutes(30));
        assertThat("Проверка размера списка", bookingMap1.size(), equalTo(0));

        booking.setStatus(Status.APPROVED);
        booking2.setStatus(Status.APPROVED);
        Map<Long, Booking> bookingMap = bookingService.findAllBookingByItemIds(List.of(itemOne.getId(), itemTwo.getId()),
                LocalDateTime.now().plusMinutes(30));

        assertThat("Проверка размера списка", bookingMap.size(), equalTo(2));
        assertThat("Проверка размера start item1", bookingMap.get(itemOne.getId()).getStart(), nullValue());
        assertThat("Проверка размера start item2", bookingMap.get(itemTwo.getId()).getStart(), nullValue());
        assertThat("Проверка размера end item1", bookingMap.get(itemOne.getId()).getEnd(), equalTo(booking.getEnd()));
        assertThat("Проверка размера end item2", bookingMap.get(itemTwo.getId()).getEnd(), equalTo(booking2.getEnd()));
    }
}