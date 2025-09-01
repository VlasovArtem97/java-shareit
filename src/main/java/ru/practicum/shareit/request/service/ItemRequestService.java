package ru.practicum.shareit.request.service;

import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.practicum.shareit.interfacemarker.Create;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto addItemRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getItemRequestOwner(Long userId);

    List<ItemRequestDto> getAllItemRequest(Long userId);

    ItemRequestDto getItemRequest(Long userId, Long requestId);

    ItemRequest getItemRequestWithoutDto(Long requestId);
}
