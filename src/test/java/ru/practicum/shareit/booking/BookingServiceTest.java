package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private ItemServiceImpl itemService;
    @InjectMocks
    private BookingServiceImpl bookingService;

    private Booking booking1;
    private BookingDto bookingDto;
    private BookingCreationDto bookingCreationDto;
    private User user1;
    private User user2;
    private Item item1;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@email.com")
                .build();
        user2 = User.builder()
                .id(2L)
                .name("user2")
                .email("user2@email.com")
                .build();
        ItemRequest itemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("descr")
                .requester(user1)
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();
        item1 = Item.builder()
                .id(1L)
                .name("item1")
                .description("item descr")
                .owner(user1)
                .available(true)
                .request(itemRequest1)
                .build();
        booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .end(LocalDateTime.now().plusDays(3).truncatedTo(ChronoUnit.SECONDS))
                .item(item1)
                .booker(user1)
                .status(Status.WAITING)
                .build();
        bookingDto = BookingMapper.toDto(booking1);
        bookingCreationDto = BookingCreationDto.builder()
                .start(booking1.getStart())
                .end(booking1.getEnd())
                .itemId(booking1.getItem().getId())
                .build();

        pageable = PageRequest.of(1 / 20, 20, Sort.by("start").descending());
    }

    @Test
    void shouldThrowValidationExceptionWhenPassingWrongState() {
        assertThrows(ValidationException.class, () ->
                bookingService.getByBookerId(1L, "CHIHUAHUA", 1, 20));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenPassingWrongFromAndSize() {
        assertThrows(IllegalArgumentException.class, () ->
                bookingService.getByBookerId(1L, "ALL", 0, 0));
    }

    @Test
    void shouldReturnBookingsListWhenGetByBooker1AndStateAll() {
        when(userService.getUserById(user1.getId())).thenReturn(user1);
        when(bookingRepository.findAllByBookerId(user1.getId(), pageable)).thenReturn(List.of(booking1));

        assertThat(bookingService.getByBookerId(user1.getId(), "ALL", 1, 20)).asList()
                .contains(bookingDto);
    }

    @Test
    void shouldReturnBookingsListWhenGetByBooker1AndStateCurrent() {
        when(userService.getUserById(user1.getId())).thenReturn(user1);
        when(bookingRepository
                .findAllByBookerIdAndStateCurrent(user1.getId(), pageable)).thenReturn(List.of(booking1));

        assertThat(bookingService.getByBookerId(user1.getId(), "CURRENT", 1, 20)).asList()
                .contains(bookingDto);
    }

    @Test
    void shouldReturnBookingsListWhenGetByBooker1AndStatePast() {
        when(userService.getUserById(user1.getId())).thenReturn(user1);
        when(bookingRepository
                .findAllByBookerIdAndStatePast(user1.getId(), pageable)).thenReturn(List.of(booking1));

        assertThat(bookingService.getByBookerId(user1.getId(), "PAST", 1, 20)).asList()
                .contains(bookingDto);
    }

    @Test
    void shouldReturnBookingsListWhenGetByBooker1AndStateFuture() {
        when(userService.getUserById(user1.getId())).thenReturn(user1);
        when(bookingRepository.findAllByBookerIdAndStateFuture(user1.getId(), pageable)).thenReturn(List.of(booking1));

        assertThat(bookingService.getByBookerId(user1.getId(), "FUTURE", 1, 20)).asList()
                .contains(bookingDto);
    }

    @Test
    void shouldReturnBookingsListWhenGetByBooker1AndStateWaiting() {
        when(userService.getUserById(user1.getId())).thenReturn(user1);
        when(bookingRepository
                .findAllByBookerIdAndStatusOrderByStartDesc(user1.getId(), Status.WAITING, pageable)).thenReturn(List.of(booking1));

        assertThat(bookingService.getByBookerId(user1.getId(), "WAITING", 1, 20)).asList()
                .contains(bookingDto);
    }

    @Test
    void shouldReturnBookingsListWhenGetByBooker1AndStateRejected() {
        when(userService.getUserById(user1.getId())).thenReturn(user1);
        when(bookingRepository
                .findAllByBookerIdAndStatusOrderByStartDesc(user1.getId(), Status.REJECTED, pageable)).thenReturn(List.of(booking1));

        assertThat(bookingService.getByBookerId(user1.getId(), "REJECTED", 1, 20)).asList()
                .contains(bookingDto);
    }

    @Test
    void shouldThrowValidationExceptionWhenPassingWrongStateAndOwnerId() {
        assertThrows(ValidationException.class, () ->
                bookingService.getByOwnerId(1L, "CHICHICHI", 1, 20));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenPassingWrongFromOrSizeAndOwnerId() {
        assertThrows(IllegalArgumentException.class, () ->
                bookingService.getByOwnerId(1L, "ALL", 0, 0));
    }


    @Test
    void shouldReturnBookingsListWhenGetByOwner1AndStateAll() {
        when(userService.getUserById(user1.getId())).thenReturn(user1);
        when(bookingRepository.findAllByOwnerId(user1.getId(), pageable)).thenReturn(List.of(booking1));

        assertThat(bookingService.getByOwnerId(user1.getId(), "ALL", 1, 20)).asList()
                .contains(bookingDto);
    }

    @Test
    void shouldReturnBookingsListWhenGetByOwner1AndStateCurrent() {
        when(userService.getUserById(user1.getId())).thenReturn(user1);
        when(bookingRepository
                .findAllByOwnerIdAndStateCurrent(user1.getId(), pageable)).thenReturn(List.of(booking1));

        assertThat(bookingService.getByOwnerId(user1.getId(), "CURRENT", 1, 20)).asList()
                .contains(bookingDto);
    }

    @Test
    void shouldReturnBookingsListWhenGetByOwner1AndStatePast() {
        when(userService.getUserById(user1.getId())).thenReturn(user1);
        when(bookingRepository
                .findAllByOwnerIdAndStatePast(user1.getId(), pageable)).thenReturn(List.of(booking1));

        assertThat(bookingService.getByOwnerId(user1.getId(), "PAST", 1, 20)).asList()
                .contains(bookingDto);
    }

    @Test
    void shouldReturnBookingsListWhenGetByOwner1AndStateFuture() {
        when(userService.getUserById(user1.getId())).thenReturn(user1);
        when(bookingRepository.findAllByOwnerIdAndStateFuture(user1.getId(), pageable)).thenReturn(List.of(booking1));

        assertThat(bookingService.getByOwnerId(user1.getId(), "FUTURE", 1, 20)).asList()
                .contains(bookingDto);
    }

    @Test
    void shouldReturnBookingsListWhenGetByOwner1AndStateWaiting() {
        when(userService.getUserById(user1.getId())).thenReturn(user1);
        when(bookingRepository
                .findAllByOwnerIdAndStatus(user1.getId(), Status.WAITING, pageable)).thenReturn(List.of(booking1));

        assertThat(bookingService.getByOwnerId(user1.getId(), "WAITING", 1, 20)).asList()
                .contains(bookingDto);
    }

    @Test
    void shouldReturnBookingsListWhenGetByOwner1AndStateRejected() {
        when(userService.getUserById(user1.getId())).thenReturn(user1);
        when(bookingRepository
                .findAllByOwnerIdAndStatus(user1.getId(), Status.REJECTED, pageable)).thenReturn(List.of(booking1));

        assertThat(bookingService.getByOwnerId(user1.getId(), "REJECTED", 1, 20)).asList()
                .contains(bookingDto);
    }

    @Test
    void shouldReturnCorrectBookingWhenGettingByBookingId() {
        when(bookingRepository.findById(booking1.getId())).thenReturn(Optional.of(booking1));
        when(userService.getUserById(user1.getId())).thenReturn(user1);

        assertThat(bookingService.getBookingById(user1.getId(), booking1.getId())).isEqualTo(bookingDto);
    }

    @Test
    void shouldReturnCorrectBookingWhenCreatingNewBookingId() {
        when(userService.getUserById(user2.getId())).thenReturn(user2);
        when(itemService.getItemById(item1.getId())).thenReturn(item1);
        when(bookingRepository.save(any())).thenReturn(booking1);

        assertThat(bookingService.create(user2.getId(), bookingCreationDto)).isEqualTo(bookingDto);
    }

    @Test
    void shouldReturnApprovedBooking() {
        when(bookingRepository.findById(booking1.getId())).thenReturn(Optional.of(booking1));
        when(bookingRepository.save(Mockito.any())).thenReturn(booking1);
        when(userService.getUserById(user1.getId())).thenReturn(user1);
        bookingDto.setStatus(Status.APPROVED);

        assertThat(bookingService.approve(user1.getId(), booking1.getId(), true)).isEqualTo(bookingDto);
    }

    @Test
    void shouldReturnRejectedBooking() {
        when(bookingRepository.findById(booking1.getId())).thenReturn(Optional.of(booking1));
        when(bookingRepository.save(Mockito.any())).thenReturn(booking1);
        when(userService.getUserById(user1.getId())).thenReturn(user1);
        bookingDto.setStatus(Status.REJECTED);

        assertThat(bookingService.approve(user1.getId(), booking1.getId(), false)).isEqualTo(bookingDto);
    }
}
