package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface BookingService {

    BookingDto addBooking(Long userId, NewBookingDto newBookingDto);

    BookingDto updateBookingStatus(Long userId, Long bookingId, String approved);

    BookingDto findBookingStatus(Long userId, Long bookingId);

    List<BookingDto> findBookingUser(Long userId, String state);

    List<BookingDto> findBookingOwner(Long userId, String state);

    List<Booking> findBookingForComment(Long userId, Long itemId, LocalDateTime now);

    Map<Long, Booking> findAllBookingByItemIds(List<Long> itemIds, LocalDateTime localDateTime);
}
