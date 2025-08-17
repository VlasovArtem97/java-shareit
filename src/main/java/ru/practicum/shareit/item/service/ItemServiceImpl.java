package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final UserService userService;
    private final ItemRepository itemRepository;
    private final BookingRepository itemBookingService;
    private final CommentRepository commentRepository;


    @Transactional
    @Override
    public ItemDto getItemById(Long userId, Long itemId) {
        log.info("Получен запрос на получение Item по Id - {} от пользователя с id - {}", itemId, userId);
        userService.findUserById(userId);
        Item item = returnFindItemById(itemId);
        List<CommentDto> comments = findCommentsByItemId(itemId);
        List<Booking> bookings = itemBookingService.findBookingByItemId(itemId, LocalDateTime.now());
        Booking lastBooking = null;
        Booking nextBooking = null;

        if (!bookings.isEmpty()) {
            // Сортируем бронирования по дате начала
            bookings.sort(Comparator.comparing(Booking::getStart));

            LocalDateTime now = LocalDateTime.now();

            // Находим последнее завершенное бронирование (с endDate < now)
            lastBooking = bookings.stream()
                    .filter(b -> b.getEnd().isBefore(now))
                    .max(Comparator.comparing(Booking::getEnd))
                    .orElse(null);

            // Находим следующее бронирование (с start > now)
            nextBooking = bookings.stream()
                    .filter(b -> b.getStart().isAfter(now))
                    .min(Comparator.comparing(Booking::getStart))
                    .orElse(null);
        }

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .commentDtos(comments)
                .lastBooking(lastBooking != null ? lastBooking.getEnd() : null)
                .nextBooking(nextBooking != null ? nextBooking.getEnd() : null)
                .build();
    }



    public List<CommentDto> findCommentsByItemId(Long itemId) {
        return commentRepository.findCommentsByItemId(itemId).stream()
                .map(CommentMapper::mapToCommentDtoFromComment).toList();
    }

    @Transactional
    @Override
    public Item returnFindItemById(Long itemId) {
        return itemRepository.findItemByIdWithUser(itemId).orElseThrow(()
                -> new NotFoundException("Item c ID - " + itemId + " не найден"));
    }

    @Override
    public List<Item> getItemByUserId(Long userId) {
        return itemRepository.findItemByUser_Id(userId);
    }

    @Override
    public Collection<ItemOwnerDto> getItemOwner(Long userId) {
        log.info("Получен запрос на получения списка Item пользователя с id - {}", userId);
        userService.findUserById(userId);
        List<Item> items = itemRepository.findItemByUser_Id(userId);
        if(items.isEmpty()) {
            log.error("У данного пользователя отсутствую Item. User ID: {}", userId);
            throw new IllegalStateException("У данного пользователя отсутствуют item");
        }
        LocalDateTime now = LocalDateTime.now();
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());
        List<Booking> bookings = itemBookingService.findAllByItemIds(itemIds, now);
        Map<Long, List<Booking>> bookingsByItemId = bookings.stream()
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));
        List<ItemOwnerDto> itemOwnerDtos = new ArrayList<>();
        for (Item item : items) {
            List<Booking> itemBookings = bookingsByItemId.getOrDefault(item.getId(), Collections.emptyList());

            Optional<Booking> lastBookingOpt = itemBookings.stream()
                    .filter(b -> b.getEnd().isBefore(now))
                    .max(Comparator.comparing(Booking::getEnd));

            Optional<Booking> nextBookingOpt = itemBookings.stream()
                    .filter(b -> b.getStart().isAfter(now))
                    .min(Comparator.comparing(Booking::getStart));

            ItemOwnerDto dto = ItemOwnerDto.builder()
                    .name(item.getName())
                    .description(item.getDescription())
                    .available(item.getAvailable())
                    .endTimeUseItem(lastBookingOpt.map(Booking::getEnd).orElse(null))
                    .nextTimeUseItem(nextBookingOpt.map(Booking::getStart).orElse(null))
                    .build();
            itemOwnerDtos.add(dto);
        }
        Map<Long, List<CommentDto>> commentsByItemId = commentRepository.findCommentsOwner(itemIds).stream()
                .map(CommentMapper::mapToCommentDtoFromComment)
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        for (ItemOwnerDto itemDto : itemOwnerDtos) {
            List<CommentDto> commentDtos = commentsByItemId.getOrDefault(itemDto.getId(), Collections.emptyList());
            itemDto.setCommentDtoList(commentDtos);
        }
        log.debug("Размер списка владельца Item - {}", itemOwnerDtos.size());
        return itemOwnerDtos;
    }

    @Transactional
    @Override
    public CommentDto addComment(Long userId, Long itemId, NewCommentDto newCommentDto) {
        User user = userService.returnUserFindById(userId);
        Item item = returnFindItemById(itemId);
        List<Booking> bookings = itemBookingService.findBookingForComment(userId, itemId, newCommentDto.getCreate());
        if(bookings.isEmpty()) {
            throw new IllegalStateException("Вы не можете оставлять комментарий, так как вы не бронировали данный item");
        }
        Comment comment = commentRepository.save(CommentMapper.mapToCommentFromNewCommentDto(user, item, newCommentDto));
        return CommentMapper.mapToCommentDtoFromComment(comment);
    }

    @Transactional
    @Override
    public ItemDto addNewItem(Long userId, NewItemDto item) {
        log.info("Получен запрос на добавление нового Item: {}, пользователем с id - {}", item, userId);
        User user = userService.returnUserFindById(userId);
        Item itemNew = ItemMapper.mapToItem(item);
        itemNew.setUser(user);
        ItemDto addedItem = ItemMapper.mapToItemDto(itemRepository.save(itemNew));
        log.debug("Добавленный Item: {}", addedItem);
        return addedItem;
    }

    @Transactional
    @Override
    public ItemDto updateItem(Long userId, Long itemId, UpdateItemDto item) {
        log.info("Получен запрос на обновление данных Item: {}", item);
        userService.findUserById(userId);
        Item itemOld = returnFindItemById(itemId);
        if (itemOld.getUser().getId().equals(userId)) {
            Item updateItem = ItemMapper.mapToUpdateItem(item, itemOld);
            ItemDto mapToItemDto = ItemMapper.mapToItemDto(itemRepository.save(updateItem));
            log.debug("Обновленный Item: {}", mapToItemDto);
            return mapToItemDto;
        } else {
            log.error("Изменить данные Item может только владелец. ID пользователя от которого поступил запрос - {}," +
                    "id владельца Item - {}", userId, itemOld.getUser().getId());
            throw new ValidationException("Изменить поля Item может только владелиц Item");
        }
    }

    @Override
    public Collection<ItemDto> searchItem(Long userId, String text) {
        log.info("Получен запрос от пользователя c id - {} на поиск item в соответствии с - {}", userId, text);
        userService.findUserById(userId);
        if (text.isBlank()) {
            return new ArrayList<>();
        } else {
            Collection<ItemDto> itemsDto = itemRepository
                    .search(text)
                    .stream()
                    .map(ItemMapper::mapToItemDto)
                    .toList();
            log.debug("Список Item: {}", itemsDto);
            return itemsDto;
        }
    }
}
