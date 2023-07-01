package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.Generated;

import java.time.LocalDateTime;

@Data
@Generated
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingCreationDto {
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
}
