package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.Generated;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
@Generated
public class BookingShortDto {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long bookerId;
}
