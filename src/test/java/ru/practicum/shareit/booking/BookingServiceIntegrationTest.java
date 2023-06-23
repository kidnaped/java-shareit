package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceIntegrationTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private BookingService bookingService;

    private UserDto userDto1;
    private UserDto userDto2;
    private BookingDto currentBookingDto;
    private BookingDto pastBookingDto;
    private BookingDto futureBookingDto;
    private BookingDto waitingBookingDto;
    private BookingDto rejectedBookingDto;
    private ItemDto itemDto2;

    @BeforeEach
    void beforeEach() {
        UserDto userCreationDto1 = UserDto.builder().name("userCrDto1").email("userCrDto1@dto.com").build();
        UserDto userCreationDto2 = UserDto.builder().name("userCrDto2").email("userCrDto2@dto.com").build();
        userDto1 = userService.createUser(userCreationDto1);
        userDto2 = userService.createUser(userCreationDto2);

        ItemDto itemCreationDto1 = ItemDto.builder()
                .name("ItemCreationDto1")
                .description("Some descr")
                .available(true)
                .build();
        ItemDto itemCreationDto2 = ItemDto.builder()
                .name("ItemCreationDto2")
                .description("Some descr2")
                .available(true)
                .build();

        ItemDto itemDto1 = itemService.registerItem(userDto1.getId(), itemCreationDto1);
        itemDto2 = itemService.registerItem(userDto2.getId(), itemCreationDto2);
        itemDto1.setComments(new ArrayList<>());
        itemDto2.setComments(new ArrayList<>());

        LocalDateTime currentStart = LocalDateTime.now().minusDays(1);
        LocalDateTime currentEnd = LocalDateTime.now().plusDays(2);
        BookingCreationDto currentBookingCreationDto = new BookingCreationDto(currentStart, currentEnd, itemDto2.getId());

        LocalDateTime pastStart = LocalDateTime.now().minusDays(3);
        LocalDateTime pastEnd = LocalDateTime.now().minusDays(1);
        BookingCreationDto pastBookingCreationDto = new BookingCreationDto(pastStart, pastEnd, itemDto2.getId());

        LocalDateTime futureStart = LocalDateTime.now().plusDays(1);
        LocalDateTime futureEnd = LocalDateTime.now().plusDays(3);
        BookingCreationDto futureBookingCreationDto = new BookingCreationDto(futureStart, futureEnd, itemDto2.getId());

        BookingCreationDto waitingBookingCreationDto = new BookingCreationDto(futureStart, futureEnd, itemDto1.getId());
        BookingCreationDto rejectedBookingCreationDto = new BookingCreationDto(futureStart, futureEnd, itemDto1.getId());

        currentBookingDto = bookingService.create(userDto1.getId(), currentBookingCreationDto);
        pastBookingDto = bookingService.create(userDto1.getId(), pastBookingCreationDto);
        futureBookingDto = bookingService.create(userDto1.getId(), futureBookingCreationDto);

        waitingBookingDto = bookingService.create(userDto2.getId(), waitingBookingCreationDto);
        rejectedBookingDto = bookingService.create(userDto2.getId(), rejectedBookingCreationDto);
    }

    @Test
    void shouldReturnBookingsListContainingCertainBookingWhenGettingByBookerId1() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingCreationDto bookingInputDto = new BookingCreationDto(start, end, itemDto2.getId());

        BookingDto bookingDto = bookingService.create(userDto1.getId(), bookingInputDto);

        assertThat(bookingService.getByBookerId(userDto1.getId(), "ALL", 0, 20)).asList()
                .contains(bookingDto);
    }

    @Test
    void shouldReturnBookingListsWithRequestedStatesWhenGettingByDifferentBookerIds() {
        assertThat(bookingService.getByBookerId(userDto1.getId(), "ALL", 0, 20))
                .asList().containsExactly(futureBookingDto, currentBookingDto, pastBookingDto);

        assertThat(bookingService.getByBookerId(userDto2.getId(), "ALL", 0, 20))
                .asList().containsExactly(waitingBookingDto, rejectedBookingDto);

        assertThat(bookingService.getByBookerId(userDto1.getId(), "CURRENT", 0, 20))
                .asList().containsExactly(currentBookingDto);

        assertThat(bookingService.getByBookerId(userDto1.getId(), "FUTURE", 0, 20))
                .asList().containsExactly(futureBookingDto);

        assertThat(bookingService.getByBookerId(userDto1.getId(), "PAST", 0, 20))
                .asList().containsExactly(pastBookingDto);

        bookingService.approve(userDto2.getId(), futureBookingDto.getId(), true);

        assertThat(bookingService.getByBookerId(userDto1.getId(), "WAITING", 0, 20))
                .asList().containsExactly(currentBookingDto, pastBookingDto);

        bookingService.approve(userDto2.getId(), pastBookingDto.getId(), false);
        pastBookingDto.setStatus(Status.REJECTED);

        assertThat(bookingService.getByBookerId(userDto1.getId(), "REJECTED", 0, 20))
                .asList().containsExactly(pastBookingDto);
    }

    @Test
    void shouldReturnBookingListsWithRequestedStatesWhenGettingByDifferentOwnerIds() {
        assertThat(bookingService.getByOwnerId(userDto2.getId(), "ALL", 0, 20))
                .asList().containsExactly(futureBookingDto, currentBookingDto, pastBookingDto);

        assertThat(bookingService.getByOwnerId(userDto1.getId(), "ALL", 0, 20))
                .asList().containsExactly(waitingBookingDto, rejectedBookingDto);

        assertThat(bookingService.getByOwnerId(userDto2.getId(), "CURRENT", 0, 20))
                .asList().containsExactly(currentBookingDto);

        assertThat(bookingService.getByOwnerId(userDto2.getId(), "FUTURE", 0, 20))
                .asList().containsExactly(futureBookingDto);

        assertThat(bookingService.getByOwnerId(userDto2.getId(), "PAST", 0, 20))
                .asList().containsExactly(pastBookingDto);

        bookingService.approve(userDto2.getId(), futureBookingDto.getId(), true);

        assertThat(bookingService.getByOwnerId(userDto2.getId(), "WAITING", 0, 20))
                .asList().containsExactly(currentBookingDto, pastBookingDto);

        bookingService.approve(userDto2.getId(), pastBookingDto.getId(), false);
        pastBookingDto.setStatus(Status.REJECTED);

        assertThat(bookingService.getByOwnerId(userDto2.getId(), "REJECTED", 0, 20))
                .asList().containsExactly(pastBookingDto);
    }

    @Test
    void shouldReturnCurrentBookingWhenGettingByBookingId() {
        assertThat(bookingService.getBookingById(userDto1.getId(), currentBookingDto.getId()))
                .isEqualTo(currentBookingDto);
    }

    @Test
    void shouldApproveAndReturnApprovedOrRejectedBookingWhenGettingByBookingId() {
        assertThat(bookingService.getBookingById(userDto1.getId(), currentBookingDto.getId()))
                .hasFieldOrPropertyWithValue("status", Status.WAITING);
        assertThat(bookingService.approve(userDto2.getId(), currentBookingDto.getId(), true))
                .hasFieldOrPropertyWithValue("status", Status.APPROVED);

        assertThat(bookingService.getBookingById(userDto1.getId(), futureBookingDto.getId()))
                .hasFieldOrPropertyWithValue("status", Status.WAITING);
        assertThat(bookingService.approve(userDto2.getId(), futureBookingDto.getId(), false))
                .hasFieldOrPropertyWithValue("status", Status.REJECTED);
    }
}
