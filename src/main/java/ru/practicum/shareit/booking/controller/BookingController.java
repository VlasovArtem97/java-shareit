package ru.practicum.shareit.booking.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.interfacemarker.Create;

import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody @Validated(Create.class) BookingDto newBookingDto) {
        return bookingService.addBooking(userId, newBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBookingStatus(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                          @Positive @PathVariable Long bookingId,
                                          @NotNull @RequestParam String approved) {
        return bookingService.updateBookingStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findBookingStatus(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                        @Positive @PathVariable Long bookingId) {
        return bookingService.findBookingStatus(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> findBookingUser(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findBookingUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> findBookingOwner(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findBookingOwner(userId, state);
    }
}
