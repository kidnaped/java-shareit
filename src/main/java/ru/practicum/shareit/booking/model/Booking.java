package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class Booking {
    private int id;
    private LocalDate start;
    private LocalDate end;
    @NotNull
    private Item item;
    @NotNull
    private User booker;
    private Status status;
}
