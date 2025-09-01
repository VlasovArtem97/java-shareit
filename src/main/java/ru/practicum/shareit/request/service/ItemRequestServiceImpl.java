package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserService userService;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemService itemService;

    @Transactional
    @Override
    public ItemRequestDto addItemRequest(Long userId, ItemRequestDto item) {
        log.info("Получен запрос на добавление поиска предмета для пользователя с ID={}", userId);
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(item, userService.returnUserFindById(userId));
        return itemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getItemRequestOwner(Long userId) {
        List<ItemRequestDto> itemRequest = itemRequestRepository
                .findItemRequestByUserId(userService.returnUserFindById(userId).getId()).stream()
                .map(itemRequestMapper::toItemRequestDto)
                .toList();
        if(itemRequest.isEmpty()) {
            return new ArrayList<>();
        } else {
            Map<Long,List<ItemDto>> itemDtos = itemService.
                    findItemByRequestId(itemRequest.stream().map(ItemRequestDto::getId).toList());
            for(ItemRequestDto itemRequestDto : itemRequest) {
                List<ItemDto> itemDto = itemDtos.getOrDefault(itemRequestDto.getId(), Collections.emptyList());
                itemRequestDto.setItems(itemDto);
            }
            return itemRequest.stream()
                    .sorted(Comparator.comparing(ItemRequestDto::getId).reversed())
                    .toList();
        }
    }

    @Override
    public List<ItemRequestDto> getAllItemRequest(Long userId) {
        userService.returnUserFindById(userId);
        return itemRequestRepository.findItemRequestOrderByIdDESC().stream()
                .map(itemRequestMapper::toItemRequestDto)
                .toList();
    }

    @Override
    public ItemRequestDto getItemRequest(Long userId, Long requestId) {
        userService.returnUserFindById(userId);
        ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDto(itemRequestRepository.findItemRequest(requestId));
        log.debug("ItemRequest: {}", itemRequestDto);
        return itemRequestDto;
    }

    @Override
    public ItemRequest getItemRequestWithoutDto(Long requestId) {
        return itemRequestRepository.findItemRequest(requestId);
    }
}
