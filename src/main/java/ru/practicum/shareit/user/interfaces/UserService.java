package ru.practicum.shareit.user.interfaces;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    public UserDto addUser(UserDto userDto);

    public UserDto updateUser(UserDto userDto);

    public List<UserDto> getAllUsers();

    public UserDto getUser(long userId);

    public void removeUser(long userId);
}
