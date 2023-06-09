package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

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
            throw new NotFoundException("Trying to approve not owned item for booking.");
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
    public List<BookingDto> getByBookerId(Long bookerId, String passedState, Integer from, Integer size) {
        User booker = userService.getUserById(bookerId);
        State state = State.valueOf(passedState);
        Pageable pageable = getPage(from, size);
        List<Booking> bookings = new ArrayList<>();

        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookerId(bookerId, pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStateCurrent(bookerId, pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStateFuture(bookerId, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndStatePast(bookerId, pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, Status.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, Status.REJECTED, pageable);
                break;
        }

        log.info("{}'s {} bookings found.", booker.getName(), bookings.size());
        return BookingMapper.toDto(bookings);
    }

    @Override
    public List<BookingDto> getByOwnerId(Long ownerId, String passedState, Integer from, Integer size) {
        User owner = userService.getUserById(ownerId);
        Pageable pageable = getPage(from, size);
        State state = State.valueOf(passedState);
        List<Booking> bookings = new ArrayList<>();

        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByOwnerId(ownerId, pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByOwnerIdAndStateCurrent(ownerId, pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByOwnerIdAndStateFuture(ownerId, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findAllByOwnerIdAndStatePast(ownerId, pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByOwnerIdAndStatus(ownerId, Status.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByOwnerIdAndStatus(ownerId, Status.REJECTED, pageable);
                break;
        }

        log.info("{}'s {} bookings found.", owner.getName(), bookings.size());
        return BookingMapper.toDto(bookings);
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

    private PageRequest getPage(Integer from, Integer size) {
        return PageRequest.of(from / size, size, Sort.by("start").descending());
    }
}
