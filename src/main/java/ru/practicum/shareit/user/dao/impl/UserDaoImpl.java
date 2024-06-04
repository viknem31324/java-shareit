package ru.practicum.shareit.user.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserDaoImpl implements UserDao {
    private final Logger log = LoggerFactory.getLogger(UserDaoImpl.class);
    private Map<Long, User> users = new HashMap<>();
    private long userId = 1;

    @Override
    public List<User> findAllUsers() {
        log.info("Найдены пользователи: {}", users.values());

        return new ArrayList<>(users.values());
    }

    @Override
    public User findUserById(long id) {
        User user = users.get(id);

        log.info("Найден пользователь: {}", user);

        return user;
    }

    @Override
    public User createUser(UserDto user) {
        User currentUser = User.builder().id(userId++)
                .email(user.getEmail())
                .name(user.getName())
                .build();
        users.put(currentUser.getId(), currentUser);

        log.info("Создан пользователь: {}", currentUser);

        return currentUser;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);

        log.info("Обновленный пользователь: {}", user);
        return user;
    }

    @Override
    public User deleteUser(long id) {
        User user = findUserById(id);
        log.info("Удаляемый пользователь: {}", user);

        users.remove(id);
        return user;
    }
}
