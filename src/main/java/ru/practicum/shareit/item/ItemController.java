package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.helpers.Constant;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Validated
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
    public List<ItemDtoBooking> findAllItemsDtoForUser(@RequestHeader(name = Constant.HEADER) Long ownerId,
                                                       @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                       @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Получен запрос на поиск вещей пользователя с id: {}", ownerId);
        return itemService.findAllItemsForUser(ownerId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text,
                                     @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                     @RequestParam(defaultValue = "10") @Positive int size) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        log.info("Получен запрос на поиск вещей по переданному тексту: {}", text);
        return itemService.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable long itemId, @RequestBody CommentDtoRequest commentDtoRequest,
                                 @RequestHeader(name = Constant.HEADER) Long ownerId) {
        log.info("Получен запрос на создание комментария для вещи с id: {}, комментарий: {}, id комментатора: ",
                itemId, commentDtoRequest);
        return itemService.addComment(commentDtoRequest, itemId, ownerId);
    }
}
