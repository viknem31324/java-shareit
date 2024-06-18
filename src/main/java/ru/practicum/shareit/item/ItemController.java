package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.helpers.Constant;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final Logger log = LoggerFactory.getLogger(ItemController.class);

    @PostMapping
    public ItemDto addItem(@RequestBody ItemDto itemDto, @RequestHeader(name = Constant.HEADER) Long ownerId) {
        log.info("Получен запрос на создание новой вещи. Вещь: {}, владелец: {}", itemDto, ownerId);
        return itemService.addItem(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable long itemId, @RequestBody ItemDto itemDto,
                              @RequestHeader(name = Constant.HEADER) Long ownerId) {
        log.info("Получен запрос на обновление вещи. Вещь: {}, владелец: {}", itemDto, ownerId);
        return itemService.updateItem(itemDto, itemId, ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoBooking findItemById(@PathVariable long itemId, @RequestHeader(name = Constant.HEADER) Long userId) {
        log.info("Получен запрос на поиск вещи по id. Вещь: {}, пользователь: {}", itemId, userId);
        return itemService.findItemDtoById(itemId, userId);
    }

    @GetMapping
    public List<ItemDtoBooking> findAllItemsDtoForUser(@RequestHeader(name = Constant.HEADER) Long ownerId) {
        log.info("Получен запрос на поиск вещей пользователя с id: {}", ownerId);
        return itemService.findAllItemsForUser(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        log.info("Получен запрос на поиск вещей по переданному тексту: {}", text);
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable long itemId, @RequestBody CommentDtoRequest commentDtoRequest,
                                 @RequestHeader(name = Constant.HEADER) Long ownerId) {
        log.info("Получен запрос на создание комментария для вещи с id: {}, комментарий: {}, id комментатора: ",
                itemId, commentDtoRequest);
        return itemService.addComment(commentDtoRequest, itemId, ownerId);
    }
}
