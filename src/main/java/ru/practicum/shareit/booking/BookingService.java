package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto create(Long userId, BookingCreationDto dto);

    BookingDto approve(Long userId, Long bookingId, Boolean isApproved);

    BookingDto getBookingById(Long userId, Long bookingId);

    List<BookingDto> getByBookerId(Long bookerId, String state);

    List<BookingDto> getByOwnerId(Long ownerId, String state);
}
