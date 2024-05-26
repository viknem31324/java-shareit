package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.error.exception.NotEnoughRightsException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;

    @Autowired
    public ItemController(ItemService itemService, UserService userService) {
        this.itemService = itemService;
        this.userService = userService;
    }

    @PostMapping
    public Item addItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        User owner = userService.findUserById(ownerId);
        Item item = ItemMapper.toItem(itemDto, owner);

        return itemService.addItem(item);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@PathVariable long itemId, @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        User owner = userService.findUserById(ownerId);
        List<Item> items = itemService.findAllItemsForUser(owner);

        Optional<Item> currentItem = items.stream()
                .filter(item -> item.getId() == itemId)
                .findFirst();

        ItemDto currentItemDto = ItemDto.builder()
                .id(itemId)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();

        if (currentItem.isPresent()) {
            Item item = ItemMapper.toItem(currentItemDto, owner);
            return itemService.updateItem(item);
        } else {
            throw new NotEnoughRightsException("Пользователь не является владельцем вещи!");
        }
    }

    @GetMapping("/{itemId}")
    public ItemDto findItemById(@PathVariable long itemId) {
        return ItemMapper.toItemDto(itemService.findItemById(itemId));
    }

    @GetMapping
    public List<ItemDto> findAllItemsDtoForUser(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        User owner = userService.findUserById(ownerId);
        List<Item> items = itemService.findAllItemsForUser(owner);

        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> items = itemService.searchItems(text);

        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
