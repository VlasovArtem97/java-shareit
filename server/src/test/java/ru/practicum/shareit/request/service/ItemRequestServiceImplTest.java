package ru.practicum.shareit.request.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ItemRequestServiceImplTest {

    private final EntityManager entityManager;
    private final ItemRequestService itemRequestService;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private ItemRequestDto itemRequestDto;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private User user;
    private User userTwo;
    private List<ItemRequestDto> itemRequestDtoOwnerList;
    private LocalDateTime now;


    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        itemRequestDto = ItemRequestDto.builder()
                .description("open")
                .created(now)
                .build();

        ItemRequestDto itemRequestDtoOwnerOne = ItemRequestDto.builder()
                .description("ownerOne")
                .created(now)
                .build();

        ItemRequestDto itemRequestDtoOwnerTwo = ItemRequestDto.builder()
                .description("ownerTwo")
                .created(now.plusMinutes(5L))
                .build();

        itemRequestDtoOwnerList = List.of(itemRequestDtoOwnerOne, itemRequestDtoOwnerTwo);
        user = userRepository.save(new User(null, "john@yandex.ru", "john"));
        userTwo = userRepository.save(new User(null, "alex@yandex.ru", "alex"));

    }

    @Test
    void testItemRequestMapper() {
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto, user);

        assertThat(itemRequest.getId(), nullValue());
        assertThat(itemRequest.getItem(), nullValue());
        assertThat(itemRequest.getUser(), equalTo(user));
        assertThat(itemRequest.getCreated(), equalTo(itemRequestDto.getCreated()));
        assertThat(itemRequest.getDescription(), equalTo(itemRequestDto.getDescription()));

        ItemRequest itemRequest2 = itemRequestRepository.save(itemRequest);
        Long id = itemRequest2.getId();
        String description = itemRequest2.getDescription();
        ItemRequestDto itemRequestDto1 = itemRequestMapper.toItemRequestDto(itemRequest2);
        assertThat(itemRequestDto1.getId(), equalTo(id));
        assertThat(itemRequestDto1.getItems(), nullValue());
        assertThat(itemRequestDto1.getUserId(), equalTo(user.getId()));
        assertThat(itemRequestDto1.getCreated(), equalTo(itemRequestDto.getCreated()));
        assertThat(itemRequestDto1.getDescription(), equalTo(description));

        User userThree = new User(3L, "smith@yandex.ru", "smith");
        User userFour = new User(4L, "ty@yandex.ru", "ty");
        User userFive = new User(5L, "tyy@yandex.ru", "tyy");


        Item item = Item.builder()
                .id(1L)
                .name("da")
                .description("da")
                .available(true)
                .user(userThree)
                .build();

        Item item2 = Item.builder()
                .id(2L)
                .name("da")
                .description("da")
                .available(true)
                .user(userFour)
                .build();

        ItemRequest itemRequest1 = ItemRequest.builder()
                .id(1L)
                .item(List.of(item, item2))
                .created(now)
                .description("text")
                .user(userFive)
                .build();

        ItemRequestDto itemRequestDto2 = itemRequestMapper.toItemRequestDto(itemRequest1);
        assertThat(itemRequestDto2.getId(), equalTo(itemRequest1.getId()));
        assertThat(itemRequestDto2.getItems().size(), equalTo(2));
        assertThat(itemRequestDto2.getItems().getFirst().getId(), equalTo(item.getId()));
        assertThat(itemRequestDto2.getItems().getLast().getId(), equalTo(item2.getId()));
        assertThat(itemRequestDto2.getItems().getFirst().getName(), equalTo(item.getName()));
        assertThat(itemRequestDto2.getItems().getLast().getName(), equalTo(item2.getName()));
        assertThat(itemRequestDto2.getItems().getFirst().getDescription(), equalTo(item.getDescription()));
        assertThat(itemRequestDto2.getItems().getLast().getDescription(), equalTo(item2.getDescription()));
        assertThat(itemRequestDto2.getItems().getFirst().getId(), equalTo(item.getId()));
        assertThat(itemRequestDto2.getItems().getLast().getId(), equalTo(item2.getId()));
        assertThat(itemRequestDto2.getUserId(), equalTo(userFive.getId()));
    }

    @Test
    void addItemRequest() {
        ItemRequestDto itemRequestDto1 = itemRequestService.addItemRequest(user.getId(), itemRequestDto);

        TypedQuery<ItemRequest> typedQuery = entityManager.createQuery("Select i FROM ItemRequest i WHERE i.description = :description",
                ItemRequest.class);
        ItemRequest itemRequest = typedQuery.setParameter("description", itemRequestDto.getDescription()).getSingleResult();
        assertThat("Проверка равенства id", itemRequest.getId(), equalTo(itemRequestDto1.getId()));
        assertThat("Проверка равенства description", itemRequest.getDescription(),
                equalTo(itemRequestDto.getDescription()));
        assertThat("Проверка равенства id user", itemRequest.getUser().getId(), equalTo(user.getId()));
        assertThat("Проверка времени", now, equalTo(itemRequest.getCreated()));
        assertThat("Проверка списка с items", itemRequest.getItem(), nullValue());

    }

    @Test
    void getItemRequestOwner() {
        List<ItemRequest> itemRequestList = itemRequestRepository.saveAll(
                itemRequestDtoOwnerList.stream()
                        .map(itemRequestDto1 -> itemRequestMapper.toItemRequest(itemRequestDto1, user))
                        .toList());

        List<ItemRequestDto> itemRequestDtos = itemRequestService.getItemRequestOwner(user.getId()).stream()
                .sorted(Comparator.comparing(ItemRequestDto::getId))
                .toList();

        assertThat("Проверка, что список не пустой", itemRequestDtos, not(empty()));
        assertThat("Проверка размера списка", itemRequestDtos.size(), equalTo(2));
        assertThat("Проверка равенства id", itemRequestDtos.getFirst().getId(),
                equalTo(itemRequestList.getFirst().getId()));
        assertThat("Проверка равенства id", itemRequestDtos.getLast().getId(),
                equalTo(itemRequestList.getLast().getId()));
        assertThat("Проверка равенства описания", itemRequestDtos.getFirst().getDescription(),
                equalTo(itemRequestList.getFirst().getDescription()));
        assertThat("Проверка равенства описания", itemRequestDtos.getLast().getDescription(),
                equalTo(itemRequestList.getLast().getDescription()));
        assertThat("Проверка равенства времени", itemRequestDtos.getFirst().getCreated(), equalTo(now));
        assertThat("Проверка равенства времени", itemRequestDtos.getLast().getCreated(),
                equalTo(now.plusMinutes(5L)));
        assertThat("Проверка равенства id user", itemRequestDtos.getFirst().getUserId(),
                equalTo(itemRequestList.getFirst().getUser().getId()));
        assertThat("Проверка равенства id user", itemRequestDtos.getLast().getUserId(),
                equalTo(itemRequestList.getLast().getUser().getId()));

        List<ItemRequestDto> itemRequestDtos2 = itemRequestService.getItemRequestOwner(userTwo.getId());
        assertThat("Проверка, что список пустой", itemRequestDtos2, empty());

    }

    @Test
    void getAllItemRequest() {
        List<ItemRequest> itemRequestList = itemRequestRepository.saveAll(
                itemRequestDtoOwnerList.stream()
                        .map(itemRequestDto1 -> itemRequestMapper.toItemRequest(itemRequestDto1, user))
                        .toList());
        List<ItemRequestDto> itemRequestDtos = itemRequestService.getAllItemRequest(user.getId()).stream()
                .sorted(Comparator.comparing(ItemRequestDto::getId))
                .toList();
        assertThat("Проверка, что список не пустой", itemRequestDtos, not(empty()));
        assertThat("Проверка размера списка", itemRequestDtos.size(), equalTo(2));
        assertThat("Проверка равенства id", itemRequestDtos.getFirst().getId(),
                equalTo(itemRequestList.getFirst().getId()));
        assertThat("Проверка равенства id", itemRequestDtos.getLast().getId(),
                equalTo(itemRequestList.getLast().getId()));
        assertThat("Проверка равенства описания", itemRequestDtos.getFirst().getDescription(),
                equalTo(itemRequestList.getFirst().getDescription()));
        assertThat("Проверка равенства описания", itemRequestDtos.getLast().getDescription(),
                equalTo(itemRequestList.getLast().getDescription()));
        assertThat("Проверка равенства времени", itemRequestDtos.getFirst().getCreated(), equalTo(now));
        assertThat("Проверка равенства времени", itemRequestDtos.getLast().getCreated(),
                equalTo(now.plusMinutes(5L)));
        assertThat("Проверка равенства id user", itemRequestDtos.getFirst().getUserId(),
                equalTo(itemRequestList.getFirst().getUser().getId()));
        assertThat("Проверка равенства id user", itemRequestDtos.getLast().getUserId(),
                equalTo(itemRequestList.getLast().getUser().getId()));
    }

    @Test
    void getItemRequest() {
        ItemRequest itemRequest = itemRequestRepository.save(itemRequestMapper.toItemRequest(itemRequestDto, user));

        ItemRequestDto itemRequestDt = itemRequestService.getItemRequest(user.getId(), itemRequest.getId());
        assertThat("Проверка равенства id", itemRequestDt.getId(), equalTo(itemRequest.getId()));
        assertThat("Проверка равенства description", itemRequestDt.getDescription(),
                equalTo(itemRequest.getDescription()));
        assertThat("Проверка равенства id user", itemRequestDt.getUserId(), equalTo(user.getId()));
        assertThat("Проверка времени", now, equalTo(itemRequestDt.getCreated()));
        assertThat("Проверка списка с items", itemRequest.getItem(), nullValue());
    }

    @Test
    void getItemRequestWithoutDto() {
        ItemRequest itemRequest = itemRequestRepository.save(itemRequestMapper.toItemRequest(itemRequestDto, user));

        ItemRequest itemRequestDt = itemRequestService.getItemRequestWithoutDto(itemRequest.getId());
        assertThat("Проверка равенства id", itemRequestDt.getId(), equalTo(itemRequest.getId()));
        assertThat("Проверка равенства description", itemRequestDt.getDescription(),
                equalTo(itemRequest.getDescription()));
        assertThat("Проверка равенства id user", itemRequestDt.getUser().getId(), equalTo(user.getId()));
        assertThat("Проверка времени", now, equalTo(itemRequestDt.getCreated()));
        assertThat("Проверка списка с items", itemRequestDt.getItem(), nullValue());
    }
}