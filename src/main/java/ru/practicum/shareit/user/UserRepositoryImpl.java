package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DataException;
import ru.practicum.shareit.user.interfaces.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private long id = 0;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {

        validEmailRegistered(user.getEmail());

        user.setId(getId());
        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User updateUser(User user) {

        validUserNotRegistered(user.getId());

        if (user.getName() == null) {
            user.setName(users.get(user.getId()).getName());
        }

        if (user.getEmail() == null) {
            user.setEmail(users.get(user.getId()).getEmail());
        } else {
            if (!user.getEmail().equals(users.get(user.getId()).getEmail())) {
                validEmailRegistered(user.getEmail());
            }
        }

        users.put(user.getId(), user);

        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return List.copyOf(users.values());
    }

    @Override
    public User getUser(long userId) {

        return users.get(userId);
    }

    @Override
    public void removeUser(long userId) {
        users.remove(userId);
    }

    private long getId() {
        return ++id;
    }

    private void validEmailRegistered(String email) throws IllegalArgumentException {

        boolean registered = users.values().stream()
                .map(User::getEmail)
                .anyMatch(userEmail -> userEmail.equals(email));

        if (registered) {
            throw new IllegalArgumentException("Email " + email + " already exist");
        }
    }

    private void validUserNotRegistered(long userId) throws DataException {

        if (!users.containsKey(userId)) {
            throw new DataException("User id = " + userId + " does not exist");
        }
    }
}
