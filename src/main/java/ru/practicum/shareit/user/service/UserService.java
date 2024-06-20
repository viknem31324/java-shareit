package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> findAllUsers();

    UserDto findUserById(final long userId);

    User findById(final long userId);

    UserDto createUser(final UserDto userDto);

    UserDto updateUser(final long userId, final UserDto userDto);

    UserDto deleteUser(final long userId);
}
