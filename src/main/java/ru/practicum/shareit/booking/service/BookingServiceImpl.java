package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Transactional
    @Override
    public BookingDto create(Long userId, BookingCreationDto dto) {
        bookingTimeValidation(dto.getStart(), dto.getEnd());
        User user = userService.getUserById(userId);
        Item item = itemService.getItemById(dto.getItemId());

        if (isUserOwnerOfItem(user, item)) {
            throw new NotFoundException("Trying to book user's own item.");
        }
        if (!item.isAvailable()) {
            throw new ValidationException("Item is unavailable.");
        }

        Booking booking = bookingRepository.save(BookingMapper.fromDto(dto, user, item));
        log.info("Booking {} for {} created.", booking.getId(), item.getName());

        return BookingMapper.toDto(booking);
    }

    @Transactional
    @Override
    public BookingDto approve(Long ownerId, Long bookingId, Boolean isApproved) {
        User owner = userService.getUserById(ownerId);
        Booking booking = getBookingOrThrow(bookingId);
        Item item = booking.getItem();

        bookingStatusValidation(booking);
        if (!isUserOwnerOfItem(owner, item)) {
            throw new NotFoundException("Trying to approve not owned booking.");
        }

        booking.setStatus(isApproved ? Status.APPROVED : Status.REJECTED);
        log.info("Booking {} is {}.", booking.getId(), booking.getStatus());

        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        User user = userService.getUserById(userId);
        Booking booking = getBookingOrThrow(bookingId);
        Item item = booking.getItem();

        if (!booking.getBooker().equals(user) && !isUserOwnerOfItem(user, item)) {
            throw new NotFoundException("User is not owner of the item or its booker.");
        }

        log.info("Booking {} found.", booking.getId());
        return BookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getByBookerId(Long bookerId, String passedState) {
        User booker = userService.getUserById(bookerId);
        State state = getState(passedState);
        List<Booking> bookings = new ArrayList<>();

        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStateCurrent(bookerId);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStateFuture(bookerId);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndStatePast(bookerId);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, Status.REJECTED);
                break;
        }

        log.info("{}'s {} bookings found.", booker.getName(), bookings.size());
        return BookingMapper.toDto(bookings);
    }

    @Override
    public List<BookingDto> getByOwnerId(Long ownerId, String passedState) {
        User owner = userService.getUserById(ownerId);
        State state = getState(passedState);
        List<Booking> bookings = new ArrayList<>();

        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByOwnerId(ownerId);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByOwnerIdAndStateCurrent(ownerId);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByOwnerIdAndStateFuture(ownerId);
                break;
            case PAST:
                bookings = bookingRepository.findAllByOwnerIdAndStatePast(ownerId);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByOwnerIdAndStatus(ownerId, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByOwnerIdAndStatus(ownerId, Status.REJECTED);
                break;
        }

        log.info("{}'s {} bookings found.", owner.getName(), bookings.size());
        return BookingMapper.toDto(bookings);
    }

    private void bookingTimeValidation(LocalDateTime start, LocalDateTime end) {
        if (end.isBefore(start) || end.isEqual(start)) {
            throw new ValidationException("Start time is no before end time.");
        }
    }

    private void bookingStatusValidation(Booking booking) {
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new ValidationException("Booking status must be WAITING to approve.");
        }
    }

    private boolean isUserOwnerOfItem(User user, Item item) {
        return item.getOwner().equals(user);
    }

    private Booking getBookingOrThrow(long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking ID is not found"));
    }

    private State getState(String state) {
        try {
            return State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: " + state);
        }
    }
}
