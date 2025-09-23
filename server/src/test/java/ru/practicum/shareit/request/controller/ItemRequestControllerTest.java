package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private ItemRequestDto itemRequestDto1;

    private List<ItemRequestDto> itemRequestDtoList;

    private static final String USER_ID = "X-Sharer-User-Id";

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemRequestController)
                .build();

        itemRequestDto1 = ItemRequestDto.builder()
                .id(1L)
                .description("open")
                .userId(1L)
                .build();

        ItemRequestDto itemRequestDto2 = ItemRequestDto.builder()
                .id(2L)
                .description("close")
                .userId(2L)
                .build();

        itemRequestDtoList = List.of(itemRequestDto1, itemRequestDto2);
    }

    @Test
    void addItemRequest() throws Exception {
        when(itemRequestService.addItemRequest(anyLong(), any()))
                .thenReturn(itemRequestDto1);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(jsonPath("$.id", is(itemRequestDto1.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto1.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDto1.getCreated())))
                .andExpect(jsonPath("$.userId", is(itemRequestDto1.getUserId()), Long.class));
    }

    @Test
    void getItemRequestOwner() throws Exception {
        when(itemRequestService.getItemRequestOwner(anyLong()))
                .thenReturn(itemRequestDtoList);

        mvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemRequestDtoList)))
                .andExpect(jsonPath("$.length()").value(itemRequestDtoList.size()));
    }

    @Test
    void getAllItemRequest() throws Exception {
        when(itemRequestService.getAllItemRequest(anyLong()))
                .thenReturn(itemRequestDtoList);

        mvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemRequestDtoList)))
                .andExpect(jsonPath("$.length()").value(itemRequestDtoList.size()));
    }

    @Test
    void getItemRequest() throws Exception {
        when(itemRequestService.getItemRequest(anyLong(), anyLong()))
                .thenReturn(itemRequestDto1);

        mvc.perform(get("/requests/{requestId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto1.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto1.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDto1.getCreated())))
                .andExpect(jsonPath("$.userId", is(itemRequestDto1.getUserId()), Long.class));
    }
}