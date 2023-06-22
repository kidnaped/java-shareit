package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static BookingDto toDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart().truncatedTo(ChronoUnit.SECONDS))
                .end(booking.getEnd())
                .booker(UserMapper.toShortDto(booking.getBooker()))
                .item(ItemMapper.toShortDto(booking.getItem()))
                .status(booking.getStatus())
                .build();
    }

    public static BookingShortDto toShortDto(Booking booking) {
        return BookingShortDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    public static List<BookingDto> toDto(Iterable<Booking> bookings) {
        List<BookingDto> dtos = new ArrayList<>();
        bookings.forEach(booking -> dtos.add(toDto(booking)));
        return dtos;
    }

    public static Booking fromDto(BookingCreationDto dto,
                                  User user,
                                  Item item) {
        Booking booking = new Booking();
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        return booking;
    }
}
