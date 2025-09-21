package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;
    private static final String USER_ID = "X-Sharer-User-Id";


    @GetMapping
    public ResponseEntity<Object> getBookings(@NotNull @Positive @RequestHeader(USER_ID) long userId,
                                              @RequestParam(name = "state", defaultValue = "ALL") String stateParam) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}", stateParam, userId);
        return bookingClient.getBookings(userId, state);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBookingStatus(@NotNull @Positive @RequestHeader(USER_ID) Long userId,
                                                      @NotNull @Positive @PathVariable Long bookingId,
                                                      @NotNull @NotNull @RequestParam Boolean approved) {
        return bookingClient.updateBookingStatus(userId, bookingId, approved);
    }


    @PostMapping
    public ResponseEntity<Object> addBooking(@NotNull @Positive @RequestHeader(USER_ID) Long userId,
                                             @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.addBooking(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@NotNull @Positive @RequestHeader(USER_ID) Long userId,
                                             @NotNull @Positive @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findBookingOwner(@NotNull @Positive @RequestHeader(USER_ID) Long userId,
                                                   @RequestParam(defaultValue = "ALL") String state) {

        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new IllegalStateException("Unknown state: " + state));
        return bookingClient.getBookingsOwner(userId, bookingState);
    }

}