package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userServiceMock;

    private UserDto userDto1;
    private UserDto userDto2;

    @BeforeEach
    void beforeEach() {
        userDto1 = UserDto.builder()
                .id(1L)
                .name("UserDto")
                .email("userDto@ya.ru")
                .build();

        userDto2 = UserDto.builder()
                .id(2L)
                .name("UserDto2")
                .email("userDto2@ya.ru")
                .build();

    }

    @SneakyThrows
    @Test
    void shouldReturnListOfUsersWhenGettingAllUsers() {
        when(userServiceMock.getAllUsers()).thenReturn(List.of(userDto1, userDto2));

        mockMvc.perform(get("/users/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(userDto1.getId()))
                .andExpect(jsonPath("$[0].email").value(userDto1.getEmail()))
                .andExpect(jsonPath("$[0].name").value(userDto1.getName()))
                .andExpect(jsonPath("$[1].id").value(userDto2.getId()))
                .andExpect(jsonPath("$[1].email").value(userDto2.getEmail()))
                .andExpect(jsonPath("$[1].name").value(userDto2.getName()));
        verify(userServiceMock).getAllUsers();
    }

    @SneakyThrows
    @Test
    void shouldReturnUserDtoWhenGettingById() {
        when(userServiceMock.getById(Mockito.anyLong())).thenReturn(userDto1);

        mockMvc.perform(get("/users/{id}", userDto1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto1.getId()))
                .andExpect(jsonPath("$.email").value(userDto1.getEmail()))
                .andExpect(jsonPath("$.name").value(userDto1.getName()));
        verify(userServiceMock).getById(userDto1.getId());
    }

    @SneakyThrows
    @Test
    void shouldReturnExceptionWhenGettingByWrongUserId() {
        when(userServiceMock.getById(Mockito.anyLong())).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/users/{id}", userDto1.getId())).andExpect(status().isNotFound());
        verify(userServiceMock).getById(1L);
    }

    @SneakyThrows
    @Test
    void shouldReturnUserDtoAfterCreation() {
        when(userServiceMock.createUser(Mockito.any(UserDto.class))).thenReturn(userDto1);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto1.getId()))
                .andExpect(jsonPath("$.name").value(userDto1.getName()))
                .andExpect(jsonPath("$.email").value(userDto1.getEmail()));
        verify(userServiceMock).createUser(userDto1);
    }

    @SneakyThrows
    @Test
    void shouldUpdateAndReturnUpdatedUserDto() {
        when(userServiceMock.updateUser(Mockito.anyLong(), Mockito.any(UserDto.class))).thenReturn(userDto1);

        mockMvc.perform(patch("/users/{id}", userDto1.getId())
                        .content(objectMapper.writeValueAsString(userDto1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto1.getId()))
                .andExpect(jsonPath("$.name").value(userDto1.getName()))
                .andExpect(jsonPath("$.email").value(userDto1.getEmail()));
        verify(userServiceMock).updateUser(userDto1.getId(), userDto1);
    }

    @SneakyThrows
    @Test
    void shouldDeleteUserById() {
        mockMvc.perform(delete("/users/{id}", userDto1.getId()))
                .andExpect(status().isOk());
        verify(userServiceMock).deleteUser(userDto1.getId());
    }
}