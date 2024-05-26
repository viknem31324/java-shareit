package ru.practicum.shareit.item.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.user.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ItemDaoImpl implements ItemDao {
    private final Logger log = LoggerFactory.getLogger(ItemDaoImpl.class);
    private Map<Long, Item> items = new HashMap<>();
    private long itemId = 1;

    @Override
    public Item addItem(Item item) {
        Item currentItem = Item.builder()
                .id(itemId++)
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .request(item.getRequest() != null ? item.getRequest() : null)
                .build();

        log.info("Добавляемая вещь {}", currentItem);

        items.put(currentItem.getId(), currentItem);

        return currentItem;
    }

    @Override
    public Item updateItem(Item item) {
        log.info("Обновленная вещь {}", item);

        items.put(item.getId(), item);

        return item;
    }

    @Override
    public Item findItemById(long itemId) {
        Item item = items.get(itemId);

        log.info("Найденная вещь {}", item);

        return item;
    }

    @Override
    public List<Item> findAllItemsForUser(User owner) {
        List<Item> itemsUser = items.values().stream()
                .filter(item -> item.getOwner().equals(owner))
                .collect(Collectors.toList());

        log.info("Найденные вещи пользователя {}", itemsUser);

        return itemsUser;
    }

    @Override
    public List<Item> searchItems(String text) {
        return items.values().stream()
                .filter(item -> item.getName().toUpperCase().contains(text.toUpperCase()) ||
                        item.getDescription().toUpperCase().contains(text.toUpperCase()))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }
}
