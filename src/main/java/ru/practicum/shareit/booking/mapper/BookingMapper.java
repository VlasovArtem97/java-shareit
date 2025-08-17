package ru.practicum.shareit.booking.mapper;

import lombok.Builder;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@UtilityClass
public class BookingMapper {

    public static Booking mapToBookingFromNewBookingDto(NewBookingDto newBookingDto, Item item, User user) {
        return Booking.builder()
                .start(newBookingDto.getStart())
                .end(newBookingDto.getEnd())
                .booker(user)
                .item(item)
                .status(Status.WAITING)
                .build();
    }

    public static BookingDto mapToBookingDtoFromBooking(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(booking.getBooker())
                .item(booking.getItem())
                .status(booking.getStatus())
                .build();
    }

    public static Booking mapToBookingFromBookingDto(BookingDto bookingDto) {
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .booker(bookingDto.getBooker())
                .item(bookingDto.getItem())
                .status(bookingDto.getStatus())
                .build();
    }
}
