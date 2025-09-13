package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingService bookingService;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto addBooking(@RequestHeader(USER_ID) Long userId, @RequestBody BookingDto newBookingDto) {
        return bookingService.addBooking(userId, newBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBookingStatus(@RequestHeader(USER_ID) Long userId,
                                          @PathVariable Long bookingId,
                                          @RequestParam String approved) {
        return bookingService.updateBookingStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findBookingStatus(@RequestHeader(USER_ID) Long userId,
                                        @PathVariable Long bookingId) {
        return bookingService.findBookingStatus(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> findBookingUser(@RequestHeader(USER_ID) Long userId,
                                            @RequestParam String state) {
        return bookingService.findBookingUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> findBookingOwner(@RequestHeader(USER_ID) Long userId,
                                             @RequestParam String state) {
        return bookingService.findBookingOwner(userId, state);
    }
}
