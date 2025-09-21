package ru.practicum.shareit.item.service;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ItemBookingCommitRequestServiceImplTest {

    private final EntityManager entityManager;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final ItemBookingCommitRequestService itemBookingCommitRequestService;
    private ItemDto itemDtoTwo;
    private User user;
    private User userTwo;
    private User userThree;
    private CommentDto commentDto;
    private Item itemOne;
    private BookingDto bookingDto;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        ItemDto itemDtoOne = ItemDto.builder()
                .id(null)
                .name("open")
                .description("description1")
                .available(true)
                .lastBooking(null)
                .nextBooking(null)
                .comments(null)
                .build();

        itemDtoTwo = ItemDto.builder()
                .id(null)
                .name("item2")
                .description("description2")
                .available(true)
                .lastBooking(now.plusMinutes(5))
                .nextBooking(null)
                .comments(null)
                .build();


        user = userRepository.save(new User(null, "john@yandex.ru", "john"));
        userTwo = userRepository.save(new User(null, "Smith@yandex.ru", "smith"));
        userThree = userRepository.save(new User(null, "Alex@yandex.ru", "alex"));

        commentDto = CommentDto.builder()
                .id(null)
                .text("nice")
                .created(now.minusMinutes(23))
                .build();

        itemOne = itemRepository.save(itemMapper.toItem(itemDtoOne, user));
        bookingDto = BookingDto.builder()
                .itemId(itemDtoOne.getId())
                .start(now.minusMinutes(30))
                .end(now.minusMinutes(25))
                .build();
    }

    @Test
    void getItemOwnerById() {
        Booking booking = bookingRepository.save(bookingMapper.toBooking(bookingDto, userTwo, itemOne));
        booking.setStatus(Status.APPROVED);
        commentRepository.save(commentMapper.toComment(commentDto, userTwo, itemOne));
        List<ItemDto> itemDtos = itemBookingCommitRequestService.getItemOwnerById(user.getId());

        assertThat("Проверка списка ItemDto, что не пустой", itemDtos, notNullValue());
        assertThat("Проверка размера списка ItemDto", itemDtos.size(), equalTo(1));
        assertThat("Проверка равенства id", itemDtos.getFirst().getId(),
                equalTo(itemOne.getId()));
        assertThat("Проверка равенства name", itemDtos.getFirst().getName(),
                equalTo(itemOne.getName()));
        assertThat("Проверка равенства description", itemDtos.getFirst().getDescription(),
                equalTo(itemOne.getDescription()));
        assertThat("Проверка равенства available", itemDtos.getFirst().getAvailable(), equalTo(true));
        assertThat("Проверка размера списка comments", itemDtos.getFirst().getComments().size(), equalTo(1));
        assertThat("Проверка равенства lastBooking", itemDtos.getFirst().getLastBooking(), equalTo(booking.getEnd()));
        assertThat("Проверка nextBooking == null", itemDtos.getFirst().getNextBooking(), nullValue());
    }

    @Test
    void getItemById() {
        commentRepository.save(commentMapper.toComment(commentDto, userTwo, itemOne));
        ItemDto itemDto = itemBookingCommitRequestService.getItemById(userTwo.getId(), itemOne.getId());

        assertThat("Проверка равенства id", itemDto.getId(),
                equalTo(itemOne.getId()));
        assertThat("Проверка равенства name", itemDto.getName(),
                equalTo(itemOne.getName()));
        assertThat("Проверка равенства description", itemDto.getDescription(),
                equalTo(itemOne.getDescription()));
        assertThat("Проверка равенства available", itemDto.getAvailable(), equalTo(true));
        assertThat("Проверка размера списка comments", itemDto.getComments().size(), equalTo(1));
    }

    @Test
    void addComment() {
        Booking booking = bookingRepository.save(bookingMapper.toBooking(bookingDto, userTwo, itemOne));
        booking.setStatus(Status.APPROVED);
        CommentDto commentDto1 = itemBookingCommitRequestService.addComment(userTwo.getId(), itemOne.getId(), commentDto);

        TypedQuery<Comment> typedQuery = entityManager.createQuery("Select c FROM Comment c WHERE c.text = :text",
                Comment.class);
        Comment commentTwo = typedQuery.setParameter("text", commentDto1.getText()).getSingleResult();

        assertThat("Проверка поля id Commit", commentTwo.getId(),
                equalTo(commentDto1.getId()));
        assertThat("Проверка поля text Commit", commentTwo.getText(),
                equalTo(commentDto1.getText()));
        assertThat("Проверка поля user Commit", commentTwo.getUser().getName(),
                equalTo(commentDto1.getAuthorName()));
        assertThat("Проверка поля localDateTime Commit", commentTwo.getCreated(),
                equalTo(commentDto1.getCreated()));
        assertThrows(IllegalStateException.class, () -> itemBookingCommitRequestService.addComment(userThree.getId(), itemOne.getId(), commentDto),
                "Должно выброситься исключение при условии, что пользователь не бронировал");
    }

    @Test
    void addNewItem() {
        ItemDto itemDto = itemBookingCommitRequestService.addNewItem(userTwo.getId(), itemDtoTwo);

        TypedQuery<Item> typedQuery = entityManager.createQuery("Select i FROM Item i WHERE i.name = :name",
                Item.class);
        Item item = typedQuery.setParameter("name", itemDto.getName()).getSingleResult();
        assertThat("Проверка равенства id", item.getId(), equalTo(itemDto.getId()));
        assertThat("Проверка равенства name", item.getName(), equalTo(itemDtoTwo.getName()));
        assertThat("Проверка равенства description", item.getDescription(), equalTo(itemDtoTwo.getDescription()));
        assertThat("Проверка равенства available", item.getAvailable(), equalTo(true));
        assertThat("Проверка равенства User", item.getUser(), equalTo(userTwo));
        assertThat("Проверка request", item.getItemRequest(), nullValue());
    }
}