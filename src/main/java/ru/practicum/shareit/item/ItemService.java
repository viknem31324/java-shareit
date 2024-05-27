package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.ItemNotFoundException;
import ru.practicum.shareit.error.exception.NotEnoughRightsException;
import ru.practicum.shareit.error.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemService {
    private final ItemDao itemDao;
    private final UserService userService;

    @Autowired
    public ItemService(ItemDao itemDao, UserService userService) {
        this.itemDao = itemDao;
        this.userService = userService;
    }

    public Item addItem(ItemDto itemDto, Long ownerId) {
        User owner = userService.findUserById(ownerId);
        Item item = ItemMapper.toItem(itemDto, owner);
        validationItem(item);

        return itemDao.addItem(item);
    }

    public Item updateItem(ItemDto itemDto, Long itemId, Long ownerId) {
        User owner = userService.findUserById(ownerId);
        List<Item> items = itemDao.findAllItemsForUser(owner);

        Optional<Item> optionalItem = items.stream()
                .filter(item -> item.getId() == itemId)
                .findFirst();

        if (optionalItem.isPresent()) {
            Item item = ItemMapper.toItem(itemDto, owner);
            Item findItem = findItemById(itemId);

            Item currentItem = Item.builder()
                    .id(findItem.getId())
                    .name(item.getName() != null ? item.getName() : findItem.getName())
                    .description(item.getDescription() != null ? item.getDescription() : findItem.getDescription())
                    .available(item.getAvailable() != null ? item.getAvailable() : findItem.getAvailable())
                    .owner(item.getOwner())
                    .request(item.getRequest() != null ? item.getRequest() : null)
                    .build();
            return itemDao.updateItem(currentItem);
        } else {
            throw new NotEnoughRightsException("Пользователь не является владельцем вещи!");
        }
    }

    public Item findItemById(long itemId) {
        Item item = itemDao.findItemById(itemId);

        if (item == null) {
            throw new ItemNotFoundException("Вещь с id " + itemId + " не найдена!");
        }

        return item;
    }

    public List<ItemDto> findAllItemsForUser(Long ownerId) {
        User owner = userService.findUserById(ownerId);
        List<Item> items = itemDao.findAllItemsForUser(owner);

        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
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
