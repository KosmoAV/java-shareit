package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.interfaces.UserRepository;
import ru.practicum.shareit.user.interfaces.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto addUser(UserDto userDto) {

        User user = userRepository.addUser(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {

        User user = userRepository.updateUser(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {

        return userRepository.getAllUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(long userId) {

        return UserMapper.toUserDto(userRepository.getUser(userId));
    }

    @Override
    public void removeUser(long userId) {

        userRepository.removeUser(userId);
    }
}
