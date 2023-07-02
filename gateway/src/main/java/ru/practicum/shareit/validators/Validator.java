package ru.practicum.shareit.validators;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Validator {
    public static void bookingTimeValidation(BookingInputDto dto) {
        if (dto.getEnd().isBefore(dto.getStart()) || dto.getEnd().isEqual(dto.getStart())) {
            throw new ValidationException("Start time is not before end time.");
        }
    }

    public static void bookingStateValidation(String passedState) {
        List<String> stateNames = Stream.of(State.values()).map(Enum::name).collect(Collectors.toList());
        if (!stateNames.contains(passedState)) {
            throw new ValidationException("Unknown state: " + passedState);
        }
    }

    public static void paginationDataValidation(Integer from, Integer size) {
        if (size <= 0 || from < 0) {
            throw new IllegalArgumentException("Page size must not be less than one.");
        }
    }
}
