package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.group.OnPatch;
import ru.practicum.shareit.group.OnPost;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.Positive;


@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> addUser(@RequestBody @Validated(OnPost.class) UserDto userDto) {

        log.info("Call 'addUser': {}", userDto);

        return userClient.addUser(userDto);
    }

    @PatchMapping(value = "/{userId}")
    public ResponseEntity<Object> updateUser(@RequestBody @Validated(OnPatch.class) UserDto userDto,
                              @PathVariable @Positive long userId) {

        log.info("Call 'updateUser': {}, userId = {}", userDto, userId);

        return userClient.updateUser(userDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUser() {

        log.info("Call 'getAllUser'");

        return userClient.getAllUser();

    }

    @GetMapping(value = "/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable @Positive long userId) {

        log.info("Call 'getUser': userId={}", userId);

        return userClient.getUser(userId);
    }

    @DeleteMapping(value = "/{userId}")
    public void removeUser(@PathVariable @Positive long userId) {

        log.info("Call 'removeUser': userId={}", userId);

        userClient.removeUser(userId);
    }
}
