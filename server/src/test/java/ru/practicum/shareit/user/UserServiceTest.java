package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.exception.UserNotFoundException;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    private User user;
    private UserDto userCreated = new UserDto(1L, "test@test.com", "TestName");

    @BeforeEach
    void beforeEach() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setName("TestName");
    }

    @Test
    public void findAll_whenInvoked_shouldReturnCollectionWitchUserDto() {
        when(userRepository.findAll())
                .thenReturn(List.of(user));

        List<UserDto> users = userService.findAllUsers();
        assertThat(users.size(), is(1));
        assertThat(users.get(0).getId(), is(1L));
        assertThat(users.get(0).getName(), is(user.getName()));
        assertThat(users.get(0).getEmail(), is(user.getEmail()));
    }

    @Test
    public void findUserById_whenInvoked_shouldReturnUserDto() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        UserDto userDto = userService.findUserById(1L);

        assertUsers(user, userDto);
    }

    @Test
    public void findUserById_whenInvokedWitchUserIdNotFound_shouldShouldThrowException() {
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
                userService.findUserById(99L));

        assertThat(exception.getMessage(), is("Пользователь с id 99 не найден!"));
    }

    @Test
    public void createUser_whenCreated_shouldReturnUserDto() {
        when(userRepository.save(any()))
                .thenReturn(user);

        UserDto userDto = userService.createUser(userCreated);

        assertUsers(user, userDto);
    }

    @Test
    public void updateUser_whenUpdated_shouldReturnUpdatedUserDtoWitchOldNameAndUpdatedEmail() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        UserDto updatedUserDto = new UserDto(1L, "testUpdated@test.com", null);
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName(user.getName());
        updatedUser.setEmail(updatedUserDto.getEmail());
        when(userRepository.save(any()))
                .thenReturn(updatedUser);

        UserDto userDto = userService.updateUser(user.getId(), updatedUserDto);

        assertEquals(updatedUser.getEmail(), userDto.getEmail());
        assertEquals(user.getName(), userDto.getName());
    }

    @Test
    public void updatedUser_whenUpdatedUserIdNotFound_shouldShouldThrowException() {
        UserDto userDto = new UserDto(99L, "test@test.com", "name");
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
                userService.updateUser(userDto.getId(), userDto));

        assertThat(exception.getMessage(), is("Пользователь с id 99 не найден!"));
    }

    @Test
    public void deleteUser_whenDeleted_shouldReturnDeletedUserDto() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        UserDto userDto = userService.deleteUser(1L);

        assertUsers(user, userDto);
    }

    @Test
    public void deleteUser_whenInvokedWitchUserIdNotFound_shouldShouldThrowException() {
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
                userService.deleteUser(99L));

        assertThat(exception.getMessage(), is("Пользователь с id 99 не найден!"));
    }

    private void assertUsers(User user, UserDto userDto) {
        assertThat(user.getId(), is(userDto.getId()));
        assertThat(user.getEmail(), is(userDto.getEmail()));
        assertThat(user.getName(), is(userDto.getName()));
    }
}
