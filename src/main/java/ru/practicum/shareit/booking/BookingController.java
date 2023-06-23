package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService service;

    @PostMapping
    public BookingDto create(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                             @Valid @RequestBody BookingCreationDto dto) {
        log.info("Booking creation request received.");
        return service.create(userId, dto);
    }

    @PatchMapping("{bookingId}")
    public BookingDto approve(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                              @PathVariable Long bookingId,
                              @RequestParam(name = "approved") Boolean isApproved) {
        log.info("Booking approve request received");
        return service.approve(userId, bookingId, isApproved);
    }

    @GetMapping("{bookingId}")
    public BookingDto getBooking(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                 @PathVariable Long bookingId) {
        log.info("Get booking by ID request received.");
        return service.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(@RequestHeader(value = "X-Sharer-User-Id") Long bookerId,
                                            @RequestParam(name = "state", defaultValue = "ALL") String state,
                                            @Min(0) @RequestParam(defaultValue = "0") Integer from,
                                            @Min(1) @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get user's booking request received.");
        return service.getByBookerId(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestHeader(value = "X-Sharer-User-Id") Long ownerId,
                                             @RequestParam(name = "state", defaultValue = "ALL") String state,
                                             @Min(0) @RequestParam(defaultValue = "0") Integer from,
                                             @Min(1) @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get owner's bookings request received.");
        return service.getByOwnerId(ownerId, state, from, size);
    }
}
