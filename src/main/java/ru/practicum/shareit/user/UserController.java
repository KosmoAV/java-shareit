package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.group.OnPatch;
import ru.practicum.shareit.group.OnPost;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.interfaces.UserService;

import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto addUser(@RequestBody @Validated(OnPost.class) UserDto userDto) {

        log.info("Call 'addUser': {}", userDto);

        return userService.addUser(userDto);
    }

    @PatchMapping(value = "/{userId}")
    public UserDto updateUser(@RequestBody @Validated(OnPatch.class) UserDto userDto,
                              @PathVariable @Positive long userId) {

        log.info("Call 'updateUser': {}", userDto);

        userDto.setId(userId);

        return userService.updateUser(userDto);
    }

    @GetMapping
    public List<UserDto> getAllUser() {

        log.info("Call 'getAllUser'");

        return userService.getAllUsers();
    }

    @GetMapping(value = "/{userId}")
    public UserDto getUser(@PathVariable @Positive long userId) {

        log.info("Call 'getUser': userId={}", userId);

        return userService.getUser(userId);
    }

    @DeleteMapping(value = "/{userId}")
    public void removeUser(@PathVariable @Positive long userId) {

        log.info("Call 'removeUser': userId={}", userId);

        userService.removeUser(userId);
    }
}
