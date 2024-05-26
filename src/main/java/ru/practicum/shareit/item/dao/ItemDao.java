package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemDao {
    Item addItem(Item item);

    Item updateItem(Item item);

    Item findItemById(long itemId);

    List<Item> findAllItemsForUser(User owner);

    List<Item> searchItems(String text);
}
