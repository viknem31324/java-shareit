package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.UserNotFoundException;
import ru.practicum.shareit.error.exception.ValidationUserException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<UserDto> findAllUsers() {
        return UserMapper.mapToUserDto(repository.findAll());
    }

    @Override
    public UserDto findUserById(final long userId) {
        User user = findById(userId);

        return UserMapper.mapToUserDto(user);
    }

    @Transactional
    @Override
    public UserDto createUser(final UserDto userDto) {
        validationUser(userDto);
        validationUserEmail(userDto);
        User user = repository.save(UserMapper.mapToNewUser(userDto));
        return UserMapper.mapToUserDto(user);
    }

    @Transactional
    @Override
    public UserDto updateUser(final long userId, final UserDto userDto) {
        UserDto findUser = findUserById(userId);

        if (userDto.getEmail() != null && !userDto.getEmail().equals(findUser.getEmail())) {
            validationUserEmail(userDto);
        }

        if (userDto.getEmail() == null) {
            userDto.setEmail(findUser.getEmail());
        }

        if (userDto.getName() == null) {
            userDto.setName(findUser.getName());
        }

        User user = UserMapper.mapToNewUser(userDto);
        user.setId(userId);

        User updatedUser = repository.save(user);

        return UserMapper.mapToUserDto(updatedUser);
    }

    @Transactional
    @Override
    public UserDto deleteUser(final long userId) {
        User userFind = UserMapper.mapToNewUser(findUserById(userId));
        userFind.setId(userId);
        repository.delete(userFind);
        return UserMapper.mapToUserDto(userFind);
    }

    @Override
    public User findById(final long userId) {
        Optional<User> userOpt = repository.findById(userId);

        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("Пользовательс с id " + userId + " не найден!");
        }

        return userOpt.get();
    }

    private void validationUser(final UserDto user) {
        if (user.getEmail() == null || user.getName() == null) {
            throw new ValidationUserException("Некорректые данные пользователя!");
        }
    }

    private void validationUserEmail(final UserDto user) {
        final String REGEX_EMAIL_PATTERN = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";

        if (!patternMatches(user.getEmail(), REGEX_EMAIL_PATTERN)) {
            throw new ValidationUserException("Некорректые email пользователя!");
        }
    }

    private boolean patternMatches(final String emailAddress, final String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }
}
