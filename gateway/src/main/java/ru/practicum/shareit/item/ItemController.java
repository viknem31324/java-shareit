package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.helpers.Constant;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemClient itemClient;
    private final Logger log = LoggerFactory.getLogger(ItemController.class);

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestBody @Validated ItemDto itemDto,
                                          @RequestHeader(name = Constant.HEADER) Long ownerId) {
        log.info("Получен запрос на создание новой вещи. Вещь: {}, владелец: {}", itemDto, ownerId);
        return itemClient.addItem(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable long itemId, @RequestBody ItemDto itemDto,
                              @RequestHeader(name = Constant.HEADER) Long ownerId) {
        log.info("Получен запрос на обновление вещи. Вещь: {}, владелец: {}", itemDto, ownerId);
        return itemClient.updateItem(itemDto, itemId, ownerId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemById(@PathVariable long itemId,
                                               @RequestHeader(name = Constant.HEADER) Long userId) {
        log.info("Получен запрос на поиск вещи по id. Вещь: {}, пользователь: {}", itemId, userId);
        return itemClient.findItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllItemsDtoForUser(@RequestHeader(name = Constant.HEADER) Long ownerId,
                                                       @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                       @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Получен запрос на поиск вещей пользователя с id: {}", ownerId);
        return itemClient.findAllItemsDtoForUser(ownerId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam @Validated String text,
                                     @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                     @RequestParam(defaultValue = "10") @Positive int size) {
        if (text.isBlank()) {
            return new ResponseEntity<Object>(Collections.emptyList(), HttpStatus.OK);
        }
        log.info("Получен запрос на поиск вещей по переданному тексту: {}", text);
        return itemClient.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable long itemId,
                                             @RequestBody CommentDtoRequest commentDtoRequest,
                                             @RequestHeader(name = Constant.HEADER) long ownerId) {
        log.info("Получен запрос на создание комментария для вещи с id: {}, комментарий: {}, id комментатора: ",
                itemId, commentDtoRequest);
        return itemClient.addComment(itemId, commentDtoRequest, ownerId);
    }
}
