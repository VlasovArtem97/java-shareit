package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemSearch;
import ru.practicum.shareit.item.service.ItemBookingCommitRequestService;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @Mock
    private ItemBookingCommitRequestService itemBookingCommitRequestService;

    @InjectMocks
    private ItemController itemController;

    private final ObjectMapper mapper = JsonMapper.builder()
            .findAndAddModules()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();

    private MockMvc mvc;

    private static final String USER_ID = "X-Sharer-User-Id";

    private ItemDto itemDtoOne;

    private List<ItemDto> itemDtoList;

    private List<ItemSearch> itemSearches;

    private CommentDto commentDto;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();
        commentDto = new CommentDto(1L, "comment", "john",
                LocalDateTime.now().plusMinutes(7));

        itemDtoOne = ItemDto.builder()
                .id(1L)
                .name("item1")
                .description("description1")
                .available(true)
                .lastBooking(null)
                .nextBooking(null)
                .comments(null)
                .build();

        ItemDto itemDtoTwo = ItemDto.builder()
                .id(2L)
                .name("item2")
                .description("description2")
                .available(true)
                .lastBooking(LocalDateTime.now().plusMinutes(5))
                .nextBooking(null)
                .comments(List.of(commentDto))
                .build();

        itemDtoList = List.of(itemDtoOne, itemDtoTwo);
        itemSearches = List.of(new ItemSearch() {
            @Override
            public Long getId() {
                return 1L;
            }

            @Override
            public String getName() {
                return "item1";
            }

            @Override
            public String getDescription() {
                return "description1";
            }

            @Override
            public Boolean getAvailable() {
                return true;
            }
        });
    }

    @Test
    void getItemById() throws Exception {
        when(itemBookingCommitRequestService.getItemById(anyLong(), any()))
                .thenReturn(itemDtoOne);

        mvc.perform(get("/items/{itemId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoOne.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoOne.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoOne.getDescription())))
                .andExpect(jsonPath("$.lastBooking", is(itemDtoOne.getLastBooking())))
                .andExpect(jsonPath("$.nextBooking", is(itemDtoOne.getNextBooking())))
                .andExpect(jsonPath("$.comments", is((itemDtoOne.getComments()))));
    }

    @Test
    void getItemOwner() throws Exception {
        when(itemBookingCommitRequestService.getItemOwnerById(anyLong()))
                .thenReturn(itemDtoList);

        mvc.perform(get("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDtoList)))
                .andExpect(jsonPath("$.length()").value(itemDtoList.size()));
    }

    @Test
    void addNewItem() throws Exception {
        when(itemBookingCommitRequestService.addNewItem(anyLong(), any()))
                .thenReturn(itemDtoOne);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDtoOne))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoOne.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoOne.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoOne.getDescription())))
                .andExpect(jsonPath("$.lastBooking", is(itemDtoOne.getLastBooking())))
                .andExpect(jsonPath("$.nextBooking", is(itemDtoOne.getNextBooking())))
                .andExpect(jsonPath("$.comments", is((itemDtoOne.getComments()))));
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any()))
                .thenReturn(itemDtoOne);
        mvc.perform(patch("/items/{itemId}", 1)
                        .content(mapper.writeValueAsString(itemDtoOne))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoOne.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoOne.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoOne.getDescription())))
                .andExpect(jsonPath("$.lastBooking", is(itemDtoOne.getLastBooking())))
                .andExpect(jsonPath("$.nextBooking", is(itemDtoOne.getNextBooking())))
                .andExpect(jsonPath("$.comments", is((itemDtoOne.getComments()))));
    }

    @Test
    void searchItem() throws Exception {
        when(itemService.searchItem(anyLong(), anyString()))
                .thenReturn(itemSearches);

        mvc.perform(get("/items/search?text={text}", "text")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemSearches)))
                .andExpect(jsonPath("$.length()").value(itemSearches.size()));
    }

    @Test
    void addComment() throws Exception {
        when(itemBookingCommitRequestService.addComment(anyLong(), anyLong(), any()))
                .thenReturn(commentDto);

        mvc.perform(post("/items/{itemId}/comment", 1)
                        .content(mapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentDto.getCreated().format(formatter))));
    }
}