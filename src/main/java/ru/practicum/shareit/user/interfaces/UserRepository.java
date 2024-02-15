package ru.practicum.shareit.user.interfaces;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {

    public User addUser(User user);

    public User updateUser(User user);

    public List<User> getAllUsers();

    public User getUser(long userId);

    public void removeUser(long userId);
}
