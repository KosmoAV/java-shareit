package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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

        return userService.addUser(userDto);
    }

    @PatchMapping(value = "/{userId}")
    public UserDto updateUser(@RequestBody UserDto userDto,
                              @PathVariable long userId) {

        userDto.setId(userId);

        log.info("Call 'updateUser': {}", userDto);

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

        return userService.getUser(userId);
    }

    @DeleteMapping(value = "/{userId}")
    public void removeUser(@PathVariable long userId) {

        log.info("Call 'removeUser': userId={}", userId);

        userService.removeUser(userId);
    }
}
