package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.interfaces.UserService;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto addUser(@RequestBody UserDto userDto) {

        log.info("Call 'addUser': {}", userDto);

        validate(userDto, true);

        return userService.addUser(userDto);
    }

    @PatchMapping(value = "/{userId}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable long userId) {

        log.info("Call 'updateUser': {}", userDto);

        validateId(userId);
        validate(userDto, false);

        userDto.setId(userId);

        return userService.updateUser(userDto);
    }

    @GetMapping
    public List<UserDto> getAllUser() {

        log.info("Call 'getAllUser'");

        return userService.getAllUsers();
    }

    @GetMapping(value = "/{userId}")
    public UserDto getUser(@PathVariable long userId) {

        log.info("Call 'getUser': userId={}", userId);

        validateId(userId);

        return userService.getUser(userId);
    }

    @DeleteMapping(value = "/{userId}")
    public void removeUser(@PathVariable long userId) {

        log.info("Call 'removeUser': userId={}", userId);

        userService.removeUser(userId);
    }

    private void validate(UserDto userDto, boolean allFieldRequired) throws ValidationException {

        if (allFieldRequired) {

            validateName(userDto.getName());
            validateEmail(userDto.getEmail());

        } else {

            if (userDto.getName() == null && userDto.getEmail() == null) {
                throw new ValidationException("At least one field of user must be non-null");
            }

            if (userDto.getName() != null) {
                validateName(userDto.getName());
            }

            if (userDto.getEmail() != null) {
                validateEmail(userDto.getEmail());
            }
        }
    }

    private void validateId(Long id) throws ValidationException {
        if (id < 1) {
            throw new ValidationException("Incorrect user id '" + id + "'");
        }
    }

    private void validateName(String name) throws ValidationException {

        if (name == null || name.isBlank() || name.contains(" ")) {
            throw new ValidationException("Incorrect user name '" + name + "'");
        }
    }

    public void validateEmail(String email) throws ValidationException {

        if (email == null || email.isBlank() || email.contains(" ") ||
                email.split("@").length != 2 || email.split("@")[1].split("\\.").length != 2) {

            throw new ValidationException("Incorrect user email '" + email + "'");
        }
    }
}
