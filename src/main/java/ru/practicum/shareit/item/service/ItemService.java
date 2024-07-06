package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.*;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, Long ownerId);

    ItemDto updateItem(ItemDto itemDto, Long itemId, Long ownerId);

    ItemDtoBooking findItemDtoById(Long itemId, Long userId);

    Item findById(final Long itemId);

    List<ItemDtoBooking> findAllItemsForUser(Long ownerId, int from, int size);

    List<ItemDto> searchItems(String text, int from, int size);

    void changeItem(Item item);

    CommentDto addComment(CommentDtoRequest commentDtoRequest, Long itemId, Long authorId);
}
