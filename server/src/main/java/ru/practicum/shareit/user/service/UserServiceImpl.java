package ru.practicum.shareit.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.UserNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public List<UserDto> findAllUsers() {
        return UserMapper.mapToUserDto(repository.findAll());
    }

    @Override
    public UserDto findUserById(final long userId) {
        User user = findById(userId);
        log.debug("Найден пользователь: {}", user);
        return UserMapper.mapToUserDto(user);
    }

    @Transactional
    @Override
    public UserDto createUser(final UserDto userDto) {
//        validationUser(userDto);
//        validationUserEmail(userDto);
        User user = repository.save(UserMapper.mapToNewUser(userDto));
        log.debug("Создан пользователь: {}", user);
        return UserMapper.mapToUserDto(user);
    }

    @Transactional
    @Override
    public UserDto updateUser(final long userId, final UserDto userDto) {
        UserDto findUser = findUserById(userId);
        log.debug("Найден пользователь: {}", findUser);
//        if (userDto.getEmail() != null && !userDto.getEmail().equals(findUser.getEmail())) {
//            validationUserEmail(userDto);
//        }

        if (userDto.getEmail() == null) {
            userDto.setEmail(findUser.getEmail());
        }

        if (userDto.getName() == null) {
            userDto.setName(findUser.getName());
        }

        User user = UserMapper.mapToNewUser(userDto);
        user.setId(userId);

        User updatedUser = repository.save(user);
        log.debug("Обновленный пользователь: {}", updatedUser);
        return UserMapper.mapToUserDto(updatedUser);
    }

    @Transactional
    @Override
    public UserDto deleteUser(final long userId) {
        User userFind = UserMapper.mapToNewUser(findUserById(userId));
        log.debug("Найден пользователь: {}", userFind);
        userFind.setId(userId);
        repository.delete(userFind);
        return UserMapper.mapToUserDto(userFind);
    }

    @Override
    public User findById(final long userId) {
        Optional<User> userOpt = repository.findById(userId);
        log.debug("Найден пользователь: {}", userOpt);

        if (userOpt.isEmpty()) {
            log.debug("Пользователь с id {} не найден", userId);
            throw new UserNotFoundException("Пользователь с id " + userId + " не найден!");
        }
        log.debug("Найден пользователь: {}", userOpt.get());
        return userOpt.get();
    }

//    private void validationUser(final UserDto user) {
//        if (user.getEmail() == null || user.getName() == null) {
//            throw new ValidationUserException("Некорректые данные пользователя!");
//        }
//    }
//
//    private void validationUserEmail(final UserDto user) {
//        final String REGEX_EMAIL_PATTERN = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
//
//        if (!patternMatches(user.getEmail(), REGEX_EMAIL_PATTERN)) {
//            throw new ValidationUserException("Некорректый email пользователя!");
//        }
//    }
//
//    private boolean patternMatches(final String emailAddress, final String regexPattern) {
//        return Pattern.compile(regexPattern)
//                .matcher(emailAddress)
//                .matches();
//    }
}
