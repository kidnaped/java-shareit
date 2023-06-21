package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingCreationDto> creationDtoJacksonTester;
    @Autowired
    private JacksonTester<BookingShortDto> shortDtoJacksonTester;
    @Autowired
    private JacksonTester<BookingDto> dtoJacksonTester;

    User booker;
    User owner;
    Item item;
    Booking booking;

    @BeforeEach
    void setUp() {
        booker = User.builder()
                .id(1L)
                .name("Booker")
                .email("booker@ya.ru")
                .build();
        owner = User.builder()
                .id(2L)
                .name("Owner")
                .email("owner@ya.com")
                .build();
        item = Item.builder()
                .id(1L)
                .name("Iron")
                .description("Iron iron")
                .owner(owner)
                .available(true)
                .build();
        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .end(LocalDateTime.now().plusHours(5).truncatedTo(ChronoUnit.SECONDS))
                .status(Status.APPROVED)
                .item(item)
                .booker(booker)
                .build();
    }

    @Test
    @SneakyThrows
    void bookingDtoSerialisationTest() {
        BookingDto dto = BookingMapper.toDto(booking);
        JsonContent<BookingDto> dtoJsonContent = dtoJacksonTester.write(dto);

        assertThat(dtoJsonContent).extractingJsonPathNumberValue("$.id")
                .isEqualTo(Math.toIntExact(booking.getId()));
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.start")
                .isEqualTo(booking.getStart().toString());
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.end")
                .isEqualTo(booking.getEnd().toString());
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.status")
                .isEqualTo(booking.getStatus().toString());
        assertThat(dtoJsonContent).extractingJsonPathNumberValue("$.item.id")
                .isEqualTo(Math.toIntExact(booking.getItem().getId()));
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.item.name")
                .isEqualTo(booking.getItem().getName());
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.item.description")
                .isEqualTo(booking.getItem().getDescription());
        assertThat(dtoJsonContent).extractingJsonPathBooleanValue("$.item.available")
                .isEqualTo(booking.getItem().isAvailable());
        assertThat(dtoJsonContent).extractingJsonPathNumberValue("$.booker.id")
                .isEqualTo(Math.toIntExact(booking.getBooker().getId()));
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.booker.name")
                .isEqualTo(booking.getBooker().getName());
    }

    @Test
    @SneakyThrows
    void shortBookingDtoSerialisationTest() {
        BookingShortDto dto = BookingMapper.toShortDto(booking);
        JsonContent<BookingShortDto> dtoJsonContent = shortDtoJacksonTester.write(dto);

        assertThat(dtoJsonContent).extractingJsonPathNumberValue("$.id")
                .isEqualTo(Math.toIntExact(booking.getId()));
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.start")
                .isEqualTo(booking.getStart().toString());
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.end")
                .isEqualTo(booking.getEnd().toString());
        assertThat(dtoJsonContent).extractingJsonPathNumberValue("$.bookerId")
                .isEqualTo(Math.toIntExact(booking.getBooker().getId()));
    }

    @Test
    @SneakyThrows
    void bookingCreationDtoSerialisationTest() {
        BookingCreationDto dto = BookingCreationDto.builder()
                .start(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .end(LocalDateTime.now().plusHours(5).truncatedTo(ChronoUnit.SECONDS))
                .itemId(1L)
                .build();
        JsonContent<BookingCreationDto> dtoJsonContent = creationDtoJacksonTester.write(dto);
        Booking booking = BookingMapper.fromDto(creationDtoJacksonTester
                .parseObject(dtoJsonContent.getJson()),
                booker,
                item);

        assertThat(booking)
                .hasFieldOrPropertyWithValue("start", dto.getStart())
                .hasFieldOrPropertyWithValue("end", dto.getEnd());
    }
}
