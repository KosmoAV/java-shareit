package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.interfaces.UserRepository;
import ru.practicum.shareit.user.interfaces.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {

        userService = new UserServiceImpl(userRepository);
    }

    @Test
    public void addUserTest() {

        User user = makeUser(1L, "Alex", "Alex@mail.ru");
        when(userRepository.save(any())).thenReturn(user);

        UserDto userDto = makeUserDto(1L, "Alex", "Alex@mail.ru");
        UserDto saveUserDto = userService.addUser(userDto);

        assertThat(saveUserDto.getId(), equalTo(user.getId()));
        assertThat(saveUserDto.getName(), equalTo(user.getName()));
        assertThat(saveUserDto.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    public void updateUserTest() {

        when(userRepository.save(any())).thenAnswer(invocationOnMock -> {
            return invocationOnMock.getArgument(0, User.class);
        });

        User user = makeUser(1L, "Name", "Email@mama.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(null));

        UserDto userDto = makeUserDto(2L, "Ivan", null);

        assertThrows(DataNotFoundException.class,
                () -> userService.updateUser(userDto)
        );

        userDto.setId(1L);
        UserDto updateUser = userService.updateUser(userDto);

        assertThat(updateUser.getId(), equalTo(user.getId()));
        assertThat(updateUser.getName(), equalTo(userDto.getName()));
        assertThat(updateUser.getEmail(), equalTo(user.getEmail()));

        userDto.setName(null);
        userDto.setEmail("New@papa.ru");
        updateUser = userService.updateUser(userDto);

        assertThat(updateUser.getId(), equalTo(user.getId()));
        assertThat(updateUser.getName(), equalTo(user.getName()));
        assertThat(updateUser.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    public void getAllUsersTest() {

        User user1 = makeUser(1L, "Alex", "Alex@mail.ru");
        User user2 = makeUser(2L, "Ivan", "Ivan@mail.ru");
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserDto> userDtoList = userService.getAllUsers();

        assertThat(userDtoList.size(), equalTo(2));
        assertThat(userDtoList.get(0), notNullValue());
        assertThat(userDtoList.get(0).getId(), equalTo(user1.getId()));
        assertThat(userDtoList.get(1), notNullValue());
        assertThat(userDtoList.get(1).getId(), equalTo(user2.getId()));
    }

    @Test
    public void getUserTest() {

        User user = makeUser(1L, "Alex", "Alex@mail.ru");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(null));

        assertThrows(DataNotFoundException.class, () -> userService.getUser(2L));

        UserDto saveUserDto = userService.getUser(1L);

        assertThat(saveUserDto.getId(), equalTo(user.getId()));
        assertThat(saveUserDto.getName(), equalTo(user.getName()));
        assertThat(saveUserDto.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    public void removeUserTest() {

        userService.removeUser(1L);
        verify(userRepository).deleteById(any());
    }

    private User makeUser(Long id, String name, String email) {

        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setName(name);

        return user;
    }

    private UserDto makeUserDto(Long id, String name, String email) {

        UserDto dto = new UserDto();
        dto.setId(id);
        dto.setEmail(email);
        dto.setName(name);

        return dto;
    }
}
