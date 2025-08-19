package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewCommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ItemBookingServiceImpl implements ItemBookingCommitService {

    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public List<ItemDto> getItemOwnerById(Long userId) {
        log.info("Получен запрос на получения списка Item пользователя с id - {}", userId);
        userService.returnUserFindById(userId);
        List<ItemDto> itemOwner = itemService.getItemByUserId(userId).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
        List<Long> itemIds = itemOwner.stream()
                .map(ItemDto::getId)
                .toList();
        Map<Long, Booking> bookingMap = bookingService.findAllBookingByItemIds(itemIds, LocalDateTime.now());
        Map<Long, List<Comment>> comments = itemService.findAllCommentsItems(itemIds);
        for (ItemDto item : itemOwner) {
            Booking booking = bookingMap.get(item.getId());
            List<CommentDto> commentsItem = comments.getOrDefault(item.getId(), Collections.emptyList()).stream()
                    .map(CommentMapper::mapToCommentDtoFromComment)
                    .toList();
            if (booking != null) {
                item.setLastBooking(booking.getEnd());
                item.setNextBooking(booking.getStart());
                item.setComments(commentsItem);
            } else {
                item.setLastBooking(null);
                item.setNextBooking(null);
                item.setComments(commentsItem);
            }
        }
        return itemOwner;
    }

    @Override
    public ItemDto getItemById(Long userId, Long itemId) {
        log.info("Получен запрос на получение Item по Id - {} от пользователя с id - {}", itemId, userId);
        userService.returnUserFindById(userId);
        Item item = itemService.returnFindItemById(itemId);
        List<CommentDto> comments = itemService.findAllCommentsItems(List.of(itemId))
                .getOrDefault(item.getId(), Collections.emptyList()).stream()
                .map(CommentMapper::mapToCommentDtoFromComment)
                .toList();
        ItemDto itemDto = ItemMapper.mapToItemDto(item);
        itemDto.setComments(comments);
        log.debug("Найденный item: {}", itemDto);
        return itemDto;
    }

    @Transactional
    @Override
    public CommentDto addComment(Long userId, Long itemId, NewCommentDto newCommentDto) {
        User user = userService.returnUserFindById(userId);
        Item item = itemService.returnFindItemById(itemId);
        List<Booking> bookings = bookingService.findBookingForComment(userId, itemId, newCommentDto.getCreate());
        if (bookings.isEmpty()) {
            throw new IllegalStateException("Вы не можете оставлять комментарий, так как вы не бронировали данный item");
        }
        return itemService.addComment(user, item, newCommentDto);
    }
}
