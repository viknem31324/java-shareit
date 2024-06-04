package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.UserAlreadyExistException;
import ru.practicum.shareit.error.exception.UserNotFoundException;
import ru.practicum.shareit.error.exception.ValidationException;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserService {
    private final UserDao userDao;

    @Autowired
    public UserService(final UserDao userDao) {
        this.userDao = userDao;
    }

    public List<User> findAllUsers() {
        return userDao.findAllUsers();
    }

    public User findUserById(final long userId) {
        User user = userDao.findUserById(userId);

        if (user == null) {
            throw new UserNotFoundException("Пользовательс с id " + userId + " не найден!");
        }

        return user;
    }

    public User createUser(final UserDto userDto) {
        validationUser(userDto);
        validationUserEmail(userDto);

        return userDao.createUser(userDto);
    }

    public User updateUser(final long userId, final UserDto userDto) {
        User user = findUserById(userId);

        if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
            validationUserEmail(userDto);
        }

        User updatedUser = User.builder()
                .id(userId)
                .email(userDto.getEmail() != null ? userDto.getEmail() : user.getEmail())
                .name(userDto.getName() != null ? userDto.getName() : user.getName())
                .build();

        return userDao.updateUser(updatedUser);
    }

    public User deleteUser(final long userId) {
        findUserById(userId);

        return userDao.deleteUser(userId);
    }

    private void validationUser(final UserDto user) {
        if (user.getEmail() == null || user.getName() == null) {
            throw new ValidationException("Некорректые данные пользователя!");
        }
    }

    private void validationUserEmail(final UserDto user) {
        final String REGEX_EMAIL_PATTERN = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";

        if (!patternMatches(user.getEmail(), REGEX_EMAIL_PATTERN)) {
            throw new ValidationException("Некорректые email пользователя!");
        }

        Optional<User> optUser = findAllUsers().stream()
                .filter(item -> item.getEmail().equals(user.getEmail()))
                .findFirst();

        if (optUser.isPresent()) {
            throw new UserAlreadyExistException("Пользователь с таким email уже существует!");
        }
    }

    public boolean patternMatches(final String emailAddress, final String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }
}
