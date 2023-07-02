package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@AutoConfigureTestDatabase
class BookingRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;

    private User dbUser1;
    private User dbUser2;
    private Item dbItem1;
    private Item dbItem2;
    private Booking dbBooking;
    private Booking nextDbBooking;
    private Booking lastDbBooking;
    private Booking waitingDbBooking;
    private Booking rejectedDbBooking;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        User user1 = User.builder()
                .name("user1")
                .email("user1@mail.com")
                .build();
        dbUser1 = userRepository.save(user1);

        User user2 = User.builder()
                .name("user2")
                .email("user2@mail.com")
                .build();
        dbUser2 = userRepository.save(user2);

        Item item1 = Item.builder()
                .name("Iron")
                .description("Iron iron")
                .available(true)
                .owner(dbUser1)
                .build();
        dbItem1 = itemRepository.save(item1);

        Item item2 = Item.builder()
                .name("Copper")
                .description("Copper copper")
                .available(true)
                .owner(dbUser2)
                .build();
                new Item();
        dbItem2 = itemRepository.save(item2);

        Booking booking = Booking.builder()
                .item(dbItem2)
                .booker(dbUser1)
                .start(LocalDateTime.now().minusDays(1).truncatedTo(ChronoUnit.SECONDS))
                .end(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS))
                .status(Status.APPROVED)
                .build();
        dbBooking = bookingRepository.save(booking);

        Booking last = Booking.builder()
                .item(dbItem1)
                .booker(dbUser2)
                .start(LocalDateTime.now().minusDays(3).truncatedTo(ChronoUnit.SECONDS))
                .end(LocalDateTime.now().minusDays(1).truncatedTo(ChronoUnit.SECONDS))
                .status(Status.APPROVED)
                .build();
        lastDbBooking = bookingRepository.save(last);

        Booking next = Booking.builder()
                .item(dbItem1)
                .booker(dbUser2)
                .start(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS))
                .end(LocalDateTime.now().plusDays(3).truncatedTo(ChronoUnit.SECONDS))
                .status(Status.APPROVED)
                .build();
        nextDbBooking = bookingRepository.save(next);

        Booking waiting = Booking.builder()
                .item(dbItem1)
                .booker(dbUser2)
                .start(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS))
                .end(LocalDateTime.now().plusDays(3).truncatedTo(ChronoUnit.SECONDS))
                .status(Status.WAITING)
                .build();
        waitingDbBooking = bookingRepository.save(waiting);

        Booking rejected = Booking.builder()
                .item(dbItem1)
                .booker(dbUser2)
                .start(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS))
                .end(LocalDateTime.now().plusDays(3).truncatedTo(ChronoUnit.SECONDS))
                .status(Status.REJECTED)
                .build();
        rejectedDbBooking = bookingRepository.save(rejected);

        pageable = PageRequest.of(1 / 20, 20, Sort.by("start").descending());
    }

    @Test
    void shouldReturnBookingListAfterSavingAndFindItByBookerId1() {
        assertEquals(List.of(dbBooking), bookingRepository.findAllByBookerId(dbUser1.getId(), pageable));
        assertEquals(1, bookingRepository.findAllByBookerId(dbUser1.getId(), pageable).size());
    }

    @Test
    void shouldReturnEmptyWhenFindingBookingsByUnknownBooker() {
        assertEquals(List.of(), bookingRepository.findAllByBookerId(999L, pageable));
        assertEquals(0, bookingRepository.findAllByBookerId(999L, pageable).size());
    }

    @Test
    void shouldReturnListOf4BookingsWhenFindingByBookerId2() {
        assertEquals(List.of(nextDbBooking, waitingDbBooking, rejectedDbBooking, lastDbBooking),
                bookingRepository.findAllByBookerId(dbUser2.getId(), pageable));
        assertEquals(4, bookingRepository.findAllByBookerId(dbUser2.getId(), pageable).size());
    }

    @Test
    void shouldReturnListOf4BookingsWhenFindingByOwnerId1() {
        assertEquals(List.of(nextDbBooking, waitingDbBooking, rejectedDbBooking, lastDbBooking),
                bookingRepository.findAllByOwnerId(dbUser1.getId(), pageable));
        assertEquals(4, bookingRepository.findAllByOwnerId(dbUser1.getId(), pageable).size());
    }

    @Test
    void shouldReturnEmptyWhenFindingBookingsByUnknownOwner() {
        assertEquals(List.of(), bookingRepository.findAllByOwnerId(999L, pageable));
        assertEquals(0, bookingRepository.findAllByOwnerId(999L, pageable).size());
    }

    @Test
    void shouldReturnCorrectListWhenFindingCurrentBookingsByBooker1() {
        assertEquals(List.of(dbBooking),
                bookingRepository.findAllByBookerIdAndStateCurrent(dbUser1.getId(), pageable));
        assertEquals(1,
                bookingRepository.findAllByBookerIdAndStateCurrent(dbUser1.getId(), pageable).size());
    }

    @Test
    void shouldReturnEmptyWhenFindingCurrentBookingsByWrongBooker() {
        assertEquals(List.of(),
                bookingRepository.findAllByBookerIdAndStateCurrent(dbUser2.getId(), pageable));
        assertEquals(0,
                bookingRepository.findAllByBookerIdAndStateCurrent(dbUser2.getId(), pageable).size());
    }

    @Test
    void shouldReturnEmptyWhenFindingCurrentBookingsByWrongOwner() {
        assertEquals(List.of(),
                bookingRepository.findAllByOwnerIdAndStateCurrent(dbUser1.getId(), pageable));
        assertEquals(0,
                bookingRepository.findAllByOwnerIdAndStateCurrent(dbUser1.getId(), pageable).size());
    }

    @Test
    void shouldReturnCorrectListWhenFindingCurrentBookingsByOwner2() {
        assertEquals(List.of(dbBooking),
                bookingRepository.findAllByOwnerIdAndStateCurrent(dbUser2.getId(), pageable));
        assertEquals(1,
                bookingRepository.findAllByOwnerIdAndStateCurrent(dbUser2.getId(), pageable).size());
    }

    @Test
    void shouldReturnCorrectPastBookingWhenFindingByBooker2() {
        assertEquals(List.of(lastDbBooking),
                bookingRepository.findAllByBookerIdAndStatePast(dbUser2.getId(), pageable));
        assertEquals(1,
                bookingRepository.findAllByBookerIdAndStatePast(dbUser2.getId(), pageable).size());
    }

    @Test
    void shouldReturnEmptyWhenFindingPastBookingsByWrongBooker() {
        assertEquals(List.of(),
                bookingRepository.findAllByBookerIdAndStatePast(dbUser1.getId(), pageable));
        assertEquals(0,
                bookingRepository.findAllByBookerIdAndStatePast(dbUser1.getId(), pageable).size());
    }

    @Test
    void shouldReturnCorrectPastBookingWhenFindingByOwner1() {
        assertEquals(List.of(lastDbBooking),
                bookingRepository.findAllByOwnerIdAndStatePast(dbUser1.getId(), pageable));
        assertEquals(1,
                bookingRepository.findAllByOwnerIdAndStatePast(dbUser1.getId(), pageable).size());
    }

    @Test
    void shouldReturnEmptyWhenFindingPastBookingsByWrongOwner() {
        assertEquals(List.of(),
                bookingRepository.findAllByOwnerIdAndStatePast(dbUser2.getId(), pageable));
        assertEquals(0,
                bookingRepository.findAllByOwnerIdAndStatePast(dbUser2.getId(), pageable).size());
    }

    @Test
    void shouldReturnListOfFutureBookingsWhenFindingByBooker2() {
        assertEquals(List.of(nextDbBooking, waitingDbBooking, rejectedDbBooking),
                bookingRepository.findAllByBookerIdAndStateFuture(dbUser2.getId(), pageable));
        assertEquals(3,
                bookingRepository.findAllByBookerIdAndStateFuture(dbUser2.getId(), pageable).size());
    }

    @Test
    void shouldReturnEmptyWhenFindingFutureBookingsByWrongBooker() {
        assertEquals(List.of(),
                bookingRepository.findAllByBookerIdAndStateFuture(dbUser1.getId(), pageable));
        assertEquals(0,
                bookingRepository.findAllByBookerIdAndStateFuture(dbUser1.getId(), pageable).size());
    }

    @Test
    void shouldReturnListOfFutureBookingsWhenFindingByOwner1() {
        assertEquals(List.of(nextDbBooking, waitingDbBooking, rejectedDbBooking),
                bookingRepository.findAllByOwnerIdAndStateFuture(dbUser1.getId(), pageable));
        assertEquals(3,
                bookingRepository.findAllByOwnerIdAndStateFuture(dbUser1.getId(), pageable).size());
    }

    @Test
    void shouldReturnEmptyWhenFindingFutureBookingsByWrongOwner() {
        assertEquals(List.of(),
                bookingRepository.findAllByOwnerIdAndStateFuture(dbUser2.getId(), pageable));
        assertEquals(0,
                bookingRepository.findAllByOwnerIdAndStateFuture(dbUser2.getId(), pageable).size());
    }

    @Test
    void shouldReturnCorrectBookingWhenFindingByBooker2AndStatusWaiting() {
        assertEquals(List.of(waitingDbBooking),
                bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(dbUser2.getId(), Status.WAITING, pageable));
        assertEquals(1,
                bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(dbUser2.getId(), Status.WAITING, pageable).size());
    }

    @Test
    void shouldReturnCorrectBookingWhenFindingByBooker2AndStatusRejected() {
        assertEquals(List.of(rejectedDbBooking),
                bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(dbUser2.getId(), Status.REJECTED, pageable));
        assertEquals(1,
                bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(dbUser2.getId(), Status.REJECTED, pageable).size());
    }

    @Test
    void shouldReturnCorrectBookingWhenFindingByStatusWaitingAndOwner1() {
        assertEquals(List.of(waitingDbBooking),
                bookingRepository.findAllByOwnerIdAndStatus(dbUser1.getId(), Status.WAITING, pageable));
        assertEquals(1,
                bookingRepository.findAllByOwnerIdAndStatus(dbUser1.getId(), Status.WAITING, pageable).size());
    }

    @Test
    void shouldReturnCorrectBookingWhenFindingByStatusRejectedAndOwner1() {
        assertEquals(List.of(rejectedDbBooking),
                bookingRepository.findAllByOwnerIdAndStatus(dbUser1.getId(), Status.REJECTED, pageable));
        assertEquals(1,
                bookingRepository.findAllByOwnerIdAndStatus(dbUser1.getId(), Status.REJECTED, pageable).size());
    }

    @Test
    void shouldReturnCorrectBookingWhenFindingByItemStatusAndStartTimeBefore() {
        assertEquals(lastDbBooking, bookingRepository
                .findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(dbItem1.getId(),
                        LocalDateTime.now(),
                        Status.APPROVED).orElseThrow());
    }

    @Test
    void shouldReturnCorrectBookingWhenFindingByItemStatusAndStartTimeAfter() {
        assertEquals(nextDbBooking, bookingRepository
                .findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(dbItem1.getId(),
                        LocalDateTime.now(),
                        Status.APPROVED).orElseThrow());
    }

    @Test
    void shouldReturnTrueWhenCheckingByBooker2AndItem1() {
        assertTrue(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(dbUser2.getId(),
                dbItem1.getId(),
                LocalDateTime.now()));
    }

    @Test
    void shouldReturnFalseWhenCheckingByBooker1AndItem2() {
        assertFalse(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(dbUser1.getId(),
                dbItem2.getId(),
                LocalDateTime.now()));
    }
}