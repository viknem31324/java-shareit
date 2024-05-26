package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.ItemNotFoundException;
import ru.practicum.shareit.error.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.user.User;

import java.util.List;

@Service
public class ItemService {
    private final ItemDao itemDao;

    @Autowired
    public ItemService(ItemDao itemDao) {
        this.itemDao = itemDao;
    }

    public Item addItem(Item item) {
        validationItem(item);

        return itemDao.addItem(item);
    }

    public Item updateItem(Item item) {
        Item findItem = findItemById(item.getId());

        Item currentItem = Item.builder()
                .id(item.getId())
                .name(item.getName() != null ? item.getName() : findItem.getName())
                .description(item.getDescription() != null ? item.getDescription() : findItem.getDescription())
                .available(item.getAvailable() != null ? item.getAvailable() : findItem.getAvailable())
                .owner(item.getOwner())
                .request(item.getRequest() != null ? item.getRequest() : null)
                .build();

        return itemDao.updateItem(currentItem);
    }

    public Item findItemById(long itemId) {
        Item item = itemDao.findItemById(itemId);

        if (item == null) {
            throw new ItemNotFoundException("Вещь с id " + itemId + " не найдена!");
        }

        return item;
    }

    public List<Item> findAllItemsForUser(User owner) {
        return itemDao.findAllItemsForUser(owner);
    }

    public List<Item> searchItems(String text) {
        return itemDao.searchItems(text);
    }

    private void validationItem(Item item) {
        if (item.getAvailable() == null || item.getName() == null ||
                item.getDescription() == null || item.getName().isBlank()) {
            throw new ValidationException("Некорректные данные вещи!");
        }
    }
}
