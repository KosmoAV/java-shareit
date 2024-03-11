package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
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

        User user = userRepository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {

        User newUser = UserMapper.toUser(userDto);

        User user = userRepository.findById(newUser.getId())
                .orElseThrow(() -> new DataNotFoundException("User with id = " + newUser.getId() + " not found"));

        if (newUser.getName() != null) {
            user.setName(newUser.getName());
        }

        if (newUser.getEmail() != null) {
            user.setEmail(newUser.getEmail());
        }

        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public List<UserDto> getAllUsers() {

        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User with id = " + userId + " not found"));

        return UserMapper.toUserDto(user);
    }

    @Override
    public void removeUser(long userId) {

        userRepository.deleteById(userId);
    }
}
