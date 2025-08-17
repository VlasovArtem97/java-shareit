package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemBookingService {

    private final BookingService bookingService;

    public List<Booking> getBookingByUserId(Long userId) {
        return bookingService.findBookingByItemUserID(userId);
    }

    public List<Booking> findBookingForComment(Long userId, Long itemId, LocalDateTime localDateTime){
        return bookingService.findBookingForComment(userId, itemId, localDateTime);
    }
}
