package ru.practicum.shareit.booking.service;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.QBooking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;

    @Transactional
    @Override
    public BookingDto addBooking(Long userId, NewBookingDto newBookingDto) {
        log.info("Получен запрос на booking Item - {} от пользователя с id - {}", newBookingDto, userId);
        User user = userService.returnUserFindById(userId);
        Item item = itemService.returnFindItemById(newBookingDto.getItemId());
        if(!item.getAvailable()) {
            log.error("Item недоступен для бронирования, Item id: {}", newBookingDto.getItemId());
            throw new IllegalStateException("Item для бронирования недоступен");
        }
        validateDateTime(newBookingDto);
        Booking booking = BookingMapper.mapToBookingFromNewBookingDto(newBookingDto, item, user);
        Booking newBooking = bookingRepository.save(booking);
        log.info("Создан новый объект Booking: {}", newBooking);
        return BookingMapper.mapToBookingDtoFromBooking(newBooking);
    }

    @Transactional
    @Override
    public BookingDto updateBookingStatus(Long userId, Long bookingId, String approved) {
        log.info("Получен запрос на подтверждение/отклонения статуса booking с id - {} от пользователя с id - {}",
                bookingId, userId);
        Booking booking = bookingRepository.findBookingByIdAndBookerId(userId, bookingId).orElseThrow(()
                -> new IllegalStateException("Изменять статус Booking может только владелец Item"));
        switch(approved.toLowerCase()) {
            case "true":
                booking.setStatus(Status.APPROVED);
                break;
            case "false":
                booking.setStatus(Status.REJECTED);
                break;
            default:
                log.error("Статус параметра отличается, вместо true или false, в параметре указано: {}", approved);
                throw new ConflictException("Параметр статуса должен быть, либо true, либо false");
        }
        Booking bookingUpdateStatus = bookingRepository.save(booking);
        log.debug("Статус Booking обновлен: {}", bookingUpdateStatus);
        return BookingMapper.mapToBookingDtoFromBooking(bookingUpdateStatus);
    }

    @Transactional
    @Override
    public BookingDto findBookingStatus(Long userId, Long bookingId) {
        log.info("Получен запрос на получение информации о бронировании от пользователя с ID - {}", userId);
        userService.findUserById(userId);
        Booking booking = bookingRepository.findBookingByIdAndBookerIdAndItemUserId(userId, bookingId)
                .orElseThrow(() -> new ConflictException("Информация о бронировании может быть предоставлена " +
                        "только владельцу Item либо Создателю Booking"));
        return BookingMapper.mapToBookingDtoFromBooking(booking);
    }

    @Override
    public List<BookingDto> findBookingUser(Long userId, String state) {
        log.info("Получен запрос на получение списка booking пользователя с id - {} с параметром - {}", userId, state);
        userService.findUserById(userId);
        LocalDateTime localDateTime = LocalDateTime.now();
        List<Booking> bookings = switch (state.toLowerCase()) {
            case "all" -> bookingRepository.findBookingContainStateAll(userId);
            case "current" -> bookingRepository.findBookingContainStateCurrent(localDateTime, Status.APPROVED, userId);
            case "past" -> bookingRepository.findBookingContainStatePast(localDateTime, Status.APPROVED, userId);
            case "future" -> bookingRepository.findBookingContainStateFutureAndWaiting(localDateTime, Status.APPROVED,
                    userId);
            case "waiting" -> bookingRepository.findBookingContainStateFutureAndWaiting(localDateTime, Status.WAITING,
                    userId);
            case "rejected" -> bookingRepository.findBookingContainStateRejected(Status.REJECTED, userId);
            default -> {
                log.error("Неверный параметр запроса: {}", state);
                throw new ConflictException("Неверный параметр запроса");
            }
        };
        log.debug("Список booking - {} пользователя с id - {}", bookings, userId);
        return bookings.stream()
                .map(BookingMapper::mapToBookingDtoFromBooking)
                .toList();
    }

    @Override
    public List<BookingDto> findBookingOwner(Long userId, String state) {
        log.info("Получен запрос на получение списка booking для владельца хотя бы одной вещи, id user - {}", userId);
       userService.findUserById(userId);
       LocalDateTime localDateTime = LocalDateTime.now();
       List<Item> items = itemService.getItemByUserId(userId);
       if(items.isEmpty()) {
           log.error("У пользователя с id - {}, отсутствуют item", userId);
           throw new ConflictException("У данного пользователя отсутствуют Item");
       }
       List<Booking> bookings = switch (state.toLowerCase()) {
           case "all" -> bookingRepository.findBookingOwnerItemWithStateAll(userId);
           case "current" -> bookingRepository.findBookingOwnerItemWithStateCurrent(localDateTime, Status.APPROVED, userId);
           case "past" -> bookingRepository.findBookingOwnerItemWithStatePast(localDateTime, Status.APPROVED, userId);
           case "future" -> bookingRepository.findBookingOwnerItemWithStateFutureAndWaiting(localDateTime, Status.APPROVED,
                   userId);
           case "waiting" -> bookingRepository.findBookingOwnerItemWithStateFutureAndWaiting(localDateTime, Status.WAITING,
                   userId);
           case "rejected" -> bookingRepository.findBookingOwnerItemWithStateRejected(Status.REJECTED, userId);
           default -> {
               log.error("Ошибка в параметре запроса: {}", state);
               throw new ConflictException("Неверный параметр запроса");
           }
       };
        log.debug("Размер списка booking - {} пользователя с id - {}", bookings.size(), userId);
        return bookings.stream()
                .map(BookingMapper::mapToBookingDtoFromBooking)
                .toList();

    }

    @Transactional
    @Override
    public BookingDto findBookingById(Long bookingId) {
        log.info("Получен запрос на получение Booking c Id - {}", bookingId);
        Booking booking = bookingRepository.findBookingById(bookingId).orElseThrow(() -> new NotFoundException("Booking c " +
                "ID - " + bookingId + "не найден"));
        BookingDto bookingDto = BookingMapper.mapToBookingDtoFromBooking(booking);
        log.debug("Полученный booking: {}", bookingDto);
        return bookingDto;
    }

    @Override
    public List<Booking> findBookingByItemUserID(Long userId) {
        return bookingRepository.findByUserId(userId, LocalDateTime.now());
    }

    @Override
    public List<Booking> findBookingForComment(Long userId, Long itemId, LocalDateTime now) {
        return bookingRepository.findBookingForComment(userId, itemId, now);
    }

    @Transactional
    @Override
    public Booking returnFindBookingById(Long bookingId) {
        BookingDto bookingDto = findBookingById(bookingId);
        return BookingMapper.mapToBookingFromBookingDto(bookingDto);
    }

    private void validateDateTime(NewBookingDto newBookingDto) {
        LocalDateTime now = LocalDateTime.now().minusSeconds(5);
        if (newBookingDto.getStart() == null || newBookingDto.getEnd() == null) {
            log.error("Дата начала(конец) бронирования должна быть указана: начала - {}, конец - {}",
                    newBookingDto.getStart(), newBookingDto.getEnd());
            throw new ConflictException("Дата начала(конец) бронирования должна быть указана");
        }
        if (newBookingDto.getStart().isBefore(now)) {
            log.error("Дата начала бронирования не должна быть указана в прошедшем времени: начала - {}",
                    newBookingDto.getStart());
            throw new ConflictException("Дата начала бронирования не должна быть указана в прошедшем времени");
        }
        if (newBookingDto.getEnd().isBefore(newBookingDto.getStart())) {
            log.error("Дата окончания бронирования не должно быть раньше даты начала бронирования: начала - {}," +
                    " конец - {}", newBookingDto.getStart(), newBookingDto.getEnd());
            throw new ConflictException("Дата окончания бронирования не должно быть раньше даты начала бронирования");
        }
        if(newBookingDto.getStart().equals(newBookingDto.getEnd())) {
            log.error("Дата начала и конца бронирования совпадают: начала - {}," +
                    " конец - {}", newBookingDto.getStart(), newBookingDto.getEnd());
            throw new ConflictException("Дата начала и конца бронирования не должны быть равны");
        }
    }
}
