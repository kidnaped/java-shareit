package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = BookingController.class)
class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private BookingService bookingService;

    private BookingCreationDto creationDto;
    private BookingDto dto1;
    private BookingDto dto2;

    @BeforeEach
    void setUp() {
        creationDto = BookingCreationDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS))
                .end(LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.SECONDS))
                .build();

        BookingCreationDto creationDto2 = BookingCreationDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS))
                .end(LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.SECONDS))
                .build();

        dto1 = BookingDto.builder()
                .start(creationDto.getStart())
                .end(creationDto.getEnd())
                .item(new ItemShortDto(1L, "itemname1"))
                .booker(new UserShortDto(2L, "username1"))
                .status(Status.APPROVED)
                .build();

        dto2 = BookingDto.builder()
                .start(creationDto2.getStart())
                .end(creationDto2.getEnd())
                .item(new ItemShortDto(1L, "itemname1"))
                .booker(new UserShortDto(2L, "username1"))
                .status(Status.APPROVED)
                .build();
    }

    @Test
    @SneakyThrows
    void shouldReturnBookingDtoConnectedToUserAndItem() {
        when(bookingService.create(1L, creationDto)).thenReturn(dto1);

        mockMvc.perform(post("/bookings")
                .header("X-Sharer-User-Id", 1L)
                .content(mapper.writeValueAsString(creationDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dto1.getId()))
                .andExpect(jsonPath("$.start")
                        .value(dto1.getStart().truncatedTo(ChronoUnit.SECONDS).toString()))
                .andExpect(jsonPath("$.end").value(dto1.getEnd()
                        .truncatedTo(ChronoUnit.SECONDS).toString()))
                .andExpect(jsonPath("$.status").value(dto1.getStatus().toString()))

                .andExpect(jsonPath("$.item.id").value(dto1.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(dto1.getItem().getName()))

                .andExpect(jsonPath("$.booker.id").value(dto1.getBooker().getId()))
                .andExpect(jsonPath("$.booker.name").value(dto1.getBooker().getName()));
        verify(bookingService).create(1L, creationDto);
    }

    @Test
    @SneakyThrows
    void shouldReturnApprovedBookingConnectedWithUserAndItem() {
        when(bookingService.approve(1L, 1L, true)).thenReturn(dto1);
        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dto1.getId()))
                .andExpect(jsonPath("$.start").value(dto1.getStart()
                        .truncatedTo(ChronoUnit.SECONDS).toString()))
                .andExpect(jsonPath("$.end").value(dto1.getEnd()
                        .truncatedTo(ChronoUnit.SECONDS).toString()))
                .andExpect(jsonPath("$.status").value(dto1.getStatus().toString()))

                .andExpect(jsonPath("$.item.id").value(dto1.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(dto1.getItem().getName()))

                .andExpect(jsonPath("$.booker.id").value(dto1.getBooker().getId()))
                .andExpect(jsonPath("$.booker.name").value(dto1.getBooker().getName()));
        verify(bookingService).approve(1L, 1L, true);
    }

    @Test
    @SneakyThrows
    void ShouldReturnBookingByBookingId() {
        when(bookingService.getBookingById(1L, 1L)).thenReturn(dto1);

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dto1.getId()))
                .andExpect(jsonPath("$.start").value(dto1.getStart()
                        .truncatedTo(ChronoUnit.SECONDS).toString()))
                .andExpect(jsonPath("$.end").value(dto1.getEnd()
                        .truncatedTo(ChronoUnit.SECONDS).toString()))
                .andExpect(jsonPath("$.status").value(dto1.getStatus().toString()))

                .andExpect(jsonPath("$.item.id").value(dto1.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(dto1.getItem().getName()))

                .andExpect(jsonPath("$.booker.id").value(dto1.getBooker().getId()))
                .andExpect(jsonPath("$.booker.name").value(dto1.getBooker().getName()));
        verify(bookingService).getBookingById(1L, 1L);
    }

    @Test
    @SneakyThrows
    void shouldReturnBookingsListByBookerId() {
        when(bookingService.getByBookerId(1L, "ALL", 1, 20))
                .thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(dto1.getId()))
                .andExpect(jsonPath("$[0].start").value(dto1.getStart()
                        .truncatedTo(ChronoUnit.SECONDS).toString()))
                .andExpect(jsonPath("$[0].end").value(dto1.getEnd()
                        .truncatedTo(ChronoUnit.SECONDS).toString()))
                .andExpect(jsonPath("$[0].status").value(dto1.getStatus().toString()))

                .andExpect(jsonPath("$[0].item.id").value(dto1.getItem().getId()))
                .andExpect(jsonPath("$[0].item.name").value(dto1.getItem().getName()))

                .andExpect(jsonPath("$[0].booker.id").value(dto1.getBooker().getId()))
                .andExpect(jsonPath("$[0].booker.name").value(dto1.getBooker().getName()))

                .andExpect(jsonPath("$[1].id").value(dto2.getId()))
                .andExpect(jsonPath("$[1].start").value(dto2.getStart()
                        .truncatedTo(ChronoUnit.SECONDS).toString()))
                .andExpect(jsonPath("$[1].end").value(dto2.getEnd()
                        .truncatedTo(ChronoUnit.SECONDS).toString()))
                .andExpect(jsonPath("$[1].status").value(dto2.getStatus().toString()))

                .andExpect(jsonPath("$[1].item.id").value(dto2.getItem().getId()))
                .andExpect(jsonPath("$[1].item.name").value(dto2.getItem().getName()))

                .andExpect(jsonPath("$[1].booker.id").value(dto2.getBooker().getId()))
                .andExpect(jsonPath("$[1].booker.name").value(dto2.getBooker().getName()));
        verify(bookingService).getByBookerId(1L, "ALL", 1, 20);
    }

    @Test
    @SneakyThrows
    void shouldReturnBookingListByOwnerId() {
        when(bookingService.getByOwnerId(1L, "ALL", 1, 20))
                .thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(dto1.getId()))
                .andExpect(jsonPath("$[0].start").value(dto1.getStart()
                        .truncatedTo(ChronoUnit.SECONDS).toString()))
                .andExpect(jsonPath("$[0].end").value(dto1.getEnd()
                        .truncatedTo(ChronoUnit.SECONDS).toString()))
                .andExpect(jsonPath("$[0].status").value(dto1.getStatus().toString()))

                .andExpect(jsonPath("$[0].item.id").value(dto1.getItem().getId()))
                .andExpect(jsonPath("$[0].item.name").value(dto1.getItem().getName()))

                .andExpect(jsonPath("$[0].booker.id").value(dto1.getBooker().getId()))
                .andExpect(jsonPath("$[0].booker.name").value(dto1.getBooker().getName()))

                .andExpect(jsonPath("$[1].id").value(dto2.getId()))
                .andExpect(jsonPath("$[1].start").value(dto2.getStart()
                        .truncatedTo(ChronoUnit.SECONDS).toString()))
                .andExpect(jsonPath("$[1].end").value(dto2.getEnd()
                        .truncatedTo(ChronoUnit.SECONDS).toString()))
                .andExpect(jsonPath("$[1].status").value(dto2.getStatus().toString()))

                .andExpect(jsonPath("$[1].item.id").value(dto2.getItem().getId()))
                .andExpect(jsonPath("$[1].item.name").value(dto2.getItem().getName()))

                .andExpect(jsonPath("$[1].booker.id").value(dto2.getBooker().getId()))
                .andExpect(jsonPath("$[1].booker.name").value(dto2.getBooker().getName()));
        verify(bookingService).getByOwnerId(1L, "ALL", 1, 20);
    }
}