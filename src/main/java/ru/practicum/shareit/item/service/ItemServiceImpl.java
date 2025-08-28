package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final UserService userService;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;


    @Override
    public Item returnFindItemById(Long itemId) {
        return itemRepository.findItemByIdWithUser(itemId).orElseThrow(()
                -> new NotFoundException("Item c ID - " + itemId + " не найден"));
    }

    @Override
    public List<Item> getItemByUserId(Long userId) {
        List<Item> items = itemRepository.findItemByUser_Id(userId);
        if (items.isEmpty()) {
            log.error("У данного пользователя отсутствую Item. User ID: {}", userId);
            throw new IllegalStateException("У данного пользователя отсутствуют item");
        }
        return items;
    }

    @Override
    public Map<Long, List<Comment>> findAllCommentsItems(List<Long> itemIds) {
        return commentRepository.findCommentsOwner(itemIds).stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));
    }

    @Transactional
    @Override
    public CommentDto addComment(User user, Item item, CommentDto newCommentDto) {
        Comment comment = commentRepository.save(commentMapper.toComment(newCommentDto, user, item));
        log.debug("Добавленный комментарий: {}", comment);
        return commentMapper.toCommentDto(comment);
    }

    @Transactional
    @Override
    public ItemDto addNewItem(Long userId, ItemDto item) {
        log.info("Получен запрос на добавление нового Item: {}, пользователем с id - {}", item, userId);
        User user = userService.returnUserFindById(userId);
        Item itemNew = itemMapper.toItem(item, user);
        ItemDto addedItem = itemMapper.toItemDto(itemRepository.save(itemNew));
        log.debug("Добавленный Item: {}", addedItem);
        return addedItem;
    }

    @Transactional
    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto item) {
        log.info("Получен запрос на обновление данных Item: {}", item);
        userService.returnUserFindById(userId);
        Item itemOld = returnFindItemById(itemId);
        if (itemOld.getUser().getId().equals(userId)) {
            Item updateItem = itemMapper.toItemFromItemUpdate(item, itemOld);
            ItemDto mapToItemDto = itemMapper.toItemDto(itemRepository.save(updateItem));
            log.debug("Обновленный Item: {}", mapToItemDto);
            return mapToItemDto;
        } else {
            log.error("Изменить данные Item может только владелец. ID пользователя от которого поступил запрос - {}," +
                    "id владельца Item - {}", userId, itemOld.getUser().getId());
            throw new ValidationException("Изменить поля Item может только владелиц Item");
        }
    }

    @Override
    public Collection<ItemSearch> searchItem(Long userId, String text) {
        log.info("Получен запрос от пользователя c id - {} на поиск item в соответствии с - {}", userId, text);
        userService.returnUserFindById(userId);
        if (text.isBlank()) {
            return new ArrayList<>();
        } else {
            Collection<ItemSearch> itemsDto = itemRepository.search(text);
            log.debug("Список Item: {}", itemsDto);
            return itemsDto;
        }
    }
}
