package shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;

    private User user1;
    private User user2;
    private UserDto userDto1;
    private UserDto userDto2;
    private UserDto userCreationDto1;

    @BeforeEach
    void beforeEach() {
        user1 = User.builder()
                .id(1L)
                .name("User")
                .email("user@ya.ru")
                .build();
        userDto1 = UserMapper.toDto(user1);
        userCreationDto1 = UserDto.builder()
                .id(user1.getId())
                .name(user1.getName())
                .email(user1.getEmail())
                .build();

        user2 = User.builder()
                .id(2L)
                .name("User2")
                .email("user2@ya.ru")
                .build();
        userDto2 = UserMapper.toDto(user2);
    }

    @Test
    void shouldReturnListOfUsersWhenGettingAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        assertThat(userService.getAllUsers().size()).isEqualTo(2);
        assertThat(userService.getAllUsers()).asList().contains(userDto1, userDto2);
    }

    @Test
    void shouldReturnUserDtoWhenGettingById() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user1));

        assertThat(userService.getById(1L)).isEqualTo(userDto1);
    }

    @Test
    void shouldThrowExceptionWhenGettingByIdWithWrongId() {
        assertThrows(NotFoundException.class, () -> userService.getById(999L));
    }

    @Test
    void shouldReturnUserDtoAfterCreation() {
        when(userRepository.save(Mockito.any(User.class))).thenReturn(user1);

        assertThat(userService.createUser(userCreationDto1)).isEqualTo(userDto1);
    }

    @Test
    void shouldUpdateAndReturnUpdatedUserDto() {
        when(userRepository.save(Mockito.any(User.class))).thenReturn(user1);
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user1));

        assertThat(userService.updateUser(user1.getId(), userCreationDto1)).isEqualTo(userDto1);
    }

    @Test
    void shouldDeleteUserById() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user1));
        userService.deleteUser(user1.getId());
        verify(userRepository).deleteById(user1.getId());
    }

    @Test
    void shouldThrowExceptionWhenDeletingUserWithWrongId() {
        assertThrows(NotFoundException.class, () -> userService.deleteUser(999L));
    }

    @Test
    void shouldReturnUserWhenGettingByUserId() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user1));

        assertThat(userService.getUserById(user1.getId())).isEqualTo(user1);
    }

    @Test
    void shouldThrowExceptionWhenGettingUserByIdWithWrongId() {
        assertThrows(NotFoundException.class, () -> userService.getById(999L));
    }
}