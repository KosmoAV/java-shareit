package ru.practicum.shareit.usertests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.interfaces.UserService;

import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest()
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntegrationTests {

    private final UserService userService;

    @Test
    public void addUserTest() {

        UserDto userDto = makeUserDto("Alex", "Alex@mail.ru");

        UserDto saveUserDto = userService.addUser(userDto);

        assertThat(saveUserDto.getId(), notNullValue());
        assertThat(saveUserDto.getName(), equalTo(userDto.getName()));
        assertThat(saveUserDto.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    public void updateUserTest() {

        UserDto userDto = makeUserDto("Alex", "Alex@mail.ru");

        UserDto saveUserDto = userService.addUser(userDto);

        saveUserDto.setName("AlexNew");
        saveUserDto.setEmail("AlexNew@yandex.ru");

        UserDto updateUser = userService.updateUser(saveUserDto);

        assertThat(updateUser.getId(), equalTo(saveUserDto.getId()));
        assertThat(updateUser.getName(), equalTo(saveUserDto.getName()));
        assertThat(updateUser.getEmail(), equalTo(saveUserDto.getEmail()));
    }

    @Test
    public void getAllUsersTest() {

        List<UserDto> sourceUsers = List.of(
                makeUserDto("ivan@email", "Ivan"),
                makeUserDto("petr@email", "Petr"),
                makeUserDto("vasilii@email", "Vasilii")
        );

        for (UserDto userDto : sourceUsers) {
            userService.addUser(userDto);
        }

        List<UserDto> saveUsers = userService.getAllUsers();

        assertThat(saveUsers, hasSize(sourceUsers.size()));

        for (UserDto userDto : sourceUsers) {
            assertThat(saveUsers, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(userDto.getName())),
                    hasProperty("email", equalTo(userDto.getEmail()))
            )));
        }
    }

    @Test
    public void getUserTest() {

        UserDto userDto = makeUserDto("Alex", "Alex@mail.ru");

        UserDto createUserDto = userService.addUser(userDto);
        UserDto saveUserDto = userService.getUser(createUserDto.getId());

        assertThat(saveUserDto.getId(), notNullValue());
        assertThat(saveUserDto.getName(), equalTo(userDto.getName()));
        assertThat(saveUserDto.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    public void removeUserTest() {

        UserDto userDto = makeUserDto("Alex", "Alex@mail.ru");

        long id = userService.addUser(userDto).getId();
        userService.removeUser(id);

        DataNotFoundException e = assertThrows(DataNotFoundException.class, () -> userService.getUser(id),
                "Исключение DataNotFoundException не выброшено");

        assertThat(e.getMessage(), equalTo("User with id = " + id + " not found"));
    }

    private UserDto makeUserDto(String name, String email) {

        UserDto dto = new UserDto();
        dto.setEmail(email);
        dto.setName(name);

        return dto;
    }
}
