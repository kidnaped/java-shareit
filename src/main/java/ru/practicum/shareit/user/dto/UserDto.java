package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;

@Data
@AllArgsConstructor
@Builder
public class UserDto {
    private int id;
    private String name;
    @Email
    private String email;
}
