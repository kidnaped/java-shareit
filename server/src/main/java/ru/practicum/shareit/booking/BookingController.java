package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.Utils.USER_ID_HEADER;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService service;

    @PostMapping
    public BookingDto create(@RequestHeader(value = USER_ID_HEADER) Long userId,
                             @Valid @RequestBody BookingCreationDto dto) {
        log.info("Booking creation request with userId {}.", userId);
        return service.create(userId, dto);
    }

    @PatchMapping("{bookingId}")
    public BookingDto approve(@RequestHeader(value = USER_ID_HEADER) Long userId,
                              @PathVariable Long bookingId,
                              @RequestParam(name = "approved") Boolean isApproved) {
        log.info("Booking approve request for booking {} by user {}", bookingId, userId);
        return service.approve(userId, bookingId, isApproved);
    }

    @GetMapping("{bookingId}")
    public BookingDto getBooking(@RequestHeader(value = USER_ID_HEADER) Long userId,
                                 @PathVariable Long bookingId) {
        log.info("Get booking by ID {} for user {}.", bookingId, userId);
        return service.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(@RequestHeader(value = USER_ID_HEADER) Long bookerId,
                                            @RequestParam(name = "state", defaultValue = "ALL") String state,
                                            @RequestParam(defaultValue = "0") Integer from,
                                            @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get user's booking request received.");
        return service.getByBookerId(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestHeader(value = USER_ID_HEADER) Long ownerId,
                                             @RequestParam(name = "state", defaultValue = "ALL") String state,
                                             @RequestParam(defaultValue = "0") Integer from,
                                             @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get owner's bookings request received.");
        return service.getByOwnerId(ownerId, state, from, size);
    }
}
