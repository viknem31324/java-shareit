package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
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

    @PostMapping
    public ItemDto addItem(@RequestBody ItemDto itemDto, @RequestHeader(name = Constant.HEADER,
            required = true) Long ownerId) {
        return itemService.addItem(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable long itemId,
                           @RequestBody ItemDto itemDto,
                           @RequestHeader(name = Constant.HEADER, required = true) Long ownerId) {
        return itemService.updateItem(itemDto, itemId, ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoBooking findItemById(@PathVariable long itemId,
                                @RequestHeader(name = Constant.HEADER, required = true) Long userId) {
        return itemService.findItemDtoById(itemId, userId);
    }

    @GetMapping
    public List<ItemDtoBooking> findAllItemsDtoForUser(@RequestHeader(name = Constant.HEADER,
            required = true) Long ownerId) {
        return itemService.findAllItemsForUser(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }

        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable long itemId,
                              @RequestBody CommentDtoRequest commentDtoRequest,
                              @RequestHeader(name = Constant.HEADER, required = true) Long ownerId) {
        return itemService.addComment(commentDtoRequest, itemId, ownerId);
    }
}
