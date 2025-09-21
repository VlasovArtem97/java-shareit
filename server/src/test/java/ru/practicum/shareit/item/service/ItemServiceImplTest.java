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
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemSearch;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
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
class ItemServiceImplTest {

    private final EntityManager entityManager;
    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemRequestRepository itemRequestRepository;
    private ItemDto itemDtoOne;
    private User user;
    private User userTwo;
    private CommentDto commentDto;
    private ItemDto updateItemDto;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        itemDtoOne = ItemDto.builder()
                .id(null)
                .name("open")
                .description("description1")
                .available(true)
                .lastBooking(now)
                .nextBooking(null)
                .comments(null)
                .build();

        updateItemDto = ItemDto.builder()
                .id(null)
                .name("update")
                .description("description1")
                .available(true)
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .id(null)
                .description("description")
                .created(now)
                .build();

        user = userRepository.save(new User(null, "john@yandex.ru", "john"));
        userTwo = userRepository.save(new User(null, "Smith@yandex.ru", "smith"));
        commentDto = CommentDto.builder()
                .id(null)
                .text("nice")
                .build();

    }

    @Test
    void returnFindItemById() {
        Item itemSave = itemRepository.save(itemMapper.toItem(itemDtoOne, user));

        Item item = itemService.returnFindItemById(itemSave.getId());
        assertThat("Проверка равенства id", item.getId(), equalTo(itemSave.getId()));
        assertThat("Проверка равенства name", item.getName(), equalTo(itemDtoOne.getName()));
        assertThat("Проверка равенства description", item.getDescription(), equalTo(itemDtoOne.getDescription()));
        assertThat("Проверка равенства available", item.getAvailable(), equalTo(true));
        assertThat("Проверка равенства User", item.getUser(), equalTo(user));
        assertThat("Проверка request", item.getItemRequest(), nullValue());

        assertThrows(NotFoundException.class, () -> itemService.returnFindItemById(100L),
                "Должно выброситься исключение если Item с несуществующим id не найден");
    }

    @Test
    void getItemByUserId() {
        Item itemSave = itemRepository.save(itemMapper.toItem(itemDtoOne, user));

        List<Item> itemList = itemService.getItemByUserId(user.getId());

        assertThat("Проверка, что список не пустой", itemList, not(empty()));
        assertThat("Проверка размера списка", itemList.size(), equalTo(1));
        assertThat("Проверка равенства id", itemList.getFirst().getId(),
                equalTo(itemSave.getId()));
        assertThat("Проверка равенства name", itemList.getFirst().getName(),
                equalTo(itemDtoOne.getName()));
        assertThat("Проверка равенства description", itemList.getFirst().getDescription(),
                equalTo(itemDtoOne.getDescription()));
        assertThat("Проверка равенства available", itemList.getFirst().getAvailable(), equalTo(true));
        assertThat("Проверка равенства comments", itemList.getFirst().getItemRequest(), nullValue());

        assertThrows(IllegalStateException.class, () -> itemService.getItemByUserId(100L),
                "Должно выброситься исключение если Item с несуществующим id не найден");
    }

    @Test
    void findAllCommentsItems() {
        Item itemSave = itemRepository.save(itemMapper.toItem(itemDtoOne, user));
        Comment comment = commentRepository.save(
                commentMapper.toComment(commentDto, userTwo, itemSave));

        Map<Long, List<Comment>> itemMap = itemService.findAllCommentsItems(List.of(itemSave.getId()));
        assertThat("Проверка списка commit на null", itemMap, notNullValue());
        assertThat("Проверка размера списка commit", itemMap.size(), equalTo(1));
        assertThat("Проверка поля id Commit", itemMap.get(itemSave.getId()).getFirst().getId(),
                equalTo(comment.getId()));
        assertThat("Проверка поля text Commit", itemMap.get(itemSave.getId()).getFirst().getText(),
                equalTo(comment.getText()));
        assertThat("Проверка поля Item Commit", itemMap.get(itemSave.getId()).getFirst().getItem(),
                equalTo(comment.getItem()));
        assertThat("Проверка поля user Commit", itemMap.get(itemSave.getId()).getFirst().getUser(),
                equalTo(comment.getUser()));
        assertThat("Проверка поля localDateTime Commit", itemMap.get(itemSave.getId()).getFirst().getCreated(),
                equalTo(comment.getCreated()));
    }

    @Test
    void addComment() {
        Item itemSave = itemRepository.save(itemMapper.toItem(itemDtoOne, user));
        CommentDto comment = itemService.addComment(user, itemSave, commentDto);

        TypedQuery<Comment> typedQuery = entityManager.createQuery("Select c FROM Comment c WHERE c.text = :text",
                Comment.class);
        Comment commentTwo = typedQuery.setParameter("text", comment.getText()).getSingleResult();

        assertThat("Проверка поля id Commit", commentTwo.getId(),
                equalTo(comment.getId()));
        assertThat("Проверка поля text Commit", commentTwo.getText(),
                equalTo(comment.getText()));
        assertThat("Проверка поля user Commit", commentTwo.getUser().getName(),
                equalTo(comment.getAuthorName()));
        assertThat("Проверка поля localDateTime Commit", commentTwo.getCreated(),
                equalTo(comment.getCreated()));
    }

    @Test
    void addNewItem() {
        ItemDto itemSave = itemService.addNewItem(itemMapper.toItem(itemDtoOne, user));

        TypedQuery<Item> typedQuery = entityManager.createQuery("Select i FROM Item i WHERE i.name = :name",
                Item.class);
        Item item = typedQuery.setParameter("name", itemSave.getName()).getSingleResult();
        assertThat("Проверка равенства id", item.getId(), equalTo(itemSave.getId()));
        assertThat("Проверка равенства name", item.getName(), equalTo(itemDtoOne.getName()));
        assertThat("Проверка равенства description", item.getDescription(), equalTo(itemDtoOne.getDescription()));
        assertThat("Проверка равенства available", item.getAvailable(), equalTo(true));
        assertThat("Проверка равенства User", item.getUser(), equalTo(user));
        assertThat("Проверка request", item.getItemRequest(), nullValue());
    }

    @Test
    void updateItem() {
        Item itemSave = itemRepository.save(itemMapper.toItem(itemDtoOne, user));
        Long id = itemSave.getId();
        String name = itemSave.getName();
        String description = itemSave.getDescription();
        Boolean available = itemSave.getAvailable();
        ItemDto update = itemService.updateItem(user.getId(), itemSave.getId(), updateItemDto);
        assertThat("Проверка равенства id", update.getId(), equalTo(id));
        assertThat("Проверка равенства name", update.getName(), not(equalTo(name)));
        assertThat("Проверка равенства description", update.getDescription(), equalTo(description));
        assertThat("Проверка равенства available", update.getAvailable(), equalTo(available));
        assertThrows(ValidationException.class, () -> itemService.updateItem(userTwo.getId(), itemSave.getId(), updateItemDto),
                "Должно выброситься исключение если Item с несуществующим id не найден");

        ItemDto update2 = itemService.updateItem(user.getId(), itemSave.getId(),
                new ItemDto(null, "", "", null, null,
                        null, null, 3L));
        assertThat("Проверка равенства id", update2.getId(), equalTo(id));
        assertThat("Проверка равенства name", update2.getName(), equalTo(update.getName()));
        assertThat("Проверка равенства description", update2.getDescription(), equalTo(update.getDescription()));
        assertThat("Проверка равенства available", update2.getAvailable(), equalTo(update.getAvailable()));

    }

    @Test
    void searchItem() {
        Item itemSave = itemRepository.save(itemMapper.toItem(itemDtoOne, user));
        List<ItemSearch> itemSearches = itemService.searchItem(user.getId(), "open").stream().toList();
        assertThat("Проверка равенства id", itemSearches.getFirst().getId(), equalTo(itemSave.getId()));
        assertThat("Проверка равенства name", itemSearches.getFirst().getName(), equalTo(itemSave.getName()));
        assertThat("Проверка равенства description", itemSearches.getFirst().getDescription(),
                equalTo(itemSave.getDescription()));
        assertThat("Проверка равенства available", itemSearches.getFirst().getAvailable(),
                equalTo(itemSave.getAvailable()));

        List<ItemSearch> itemSearchesBlankText = itemService.searchItem(user.getId(), "").stream().toList();
        assertThat("Проверка пустого списка при пустом text", itemSearchesBlankText, empty());
    }

    @Test
    void findItemByRequestId() {
        ItemRequest itemRequest = itemRequestRepository.save(itemRequestMapper.toItemRequest(itemRequestDto, userTwo));
        Item itemSave = itemRepository.save(itemMapper.toItem(itemDtoOne, user));
        itemSave.setItemRequest(itemRequest);

        Map<Long, List<ItemDto>> longListMap = itemService.findItemByRequestId(List.of(itemRequest.getId()));
        assertThat("Проверка списка item на null", longListMap, notNullValue());
        assertThat("Проверка размера списка item", longListMap.size(), equalTo(1));
        assertThat("Проверка поля id item", longListMap.get(itemRequest.getId()).getFirst().getId(),
                equalTo(itemSave.getId()));
        assertThat("Проверка поля description item", longListMap.get(itemRequest.getId()).getFirst().getDescription(),
                equalTo(itemSave.getDescription()));
        assertThat("Проверка поля name item", longListMap.get(itemRequest.getId()).getFirst().getName(),
                equalTo(itemSave.getName()));
    }
}