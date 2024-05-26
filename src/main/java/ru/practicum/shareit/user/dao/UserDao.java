package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

import java.util.List;

public interface UserDao {
    User findUserById(long id);

    List<User> findAllUsers();

    User createUser(UserDto user);

    User updateUser(User user);

    User deleteUser(long id);
}
