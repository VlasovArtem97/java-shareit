package ru.practicum.shareit.booking.service;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {

    BookingDto addBooking(Long userId, NewBookingDto newBookingDto);

    BookingDto updateBookingStatus(Long userId, Long bookingId, String approved);

    BookingDto findBookingById(Long bookingId);

    Booking returnFindBookingById(Long bookingId);

    BookingDto findBookingStatus(Long userId, Long bookingId);

    List<BookingDto> findBookingUser(Long userId, String state);

    List<BookingDto> findBookingOwner(Long userId, String state);

    List<Booking> findBookingByItemUserID(Long userId);

    List<Booking> findBookingForComment(Long userId, Long itemId, LocalDateTime now);
}
