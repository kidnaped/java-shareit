package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * TODO Sprint add-item-requests.
 */

@Data
@AllArgsConstructor
@Builder
public class ItemRequestDto {
    private long id;
    @NotBlank
    private String description;
    @NotNull
    private User requester;
    private LocalDate creationDate;
}
