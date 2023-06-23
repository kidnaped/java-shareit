package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.Generated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Generated
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private long id;
    @NotBlank
    private String name;
    @Email
    @NotBlank
    private String email;
}
