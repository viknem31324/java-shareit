package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.*;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, Long ownerId);

    ItemDto updateItem(ItemDto itemDto, Long itemId, Long ownerId);

    ItemDtoBooking findItemDtoById(Long itemId, Long userId);

    Item findById(final Long itemId);

    List<ItemDtoBooking> findAllItemsForUser(Long ownerId);

    List<ItemDto> searchItems(String text);

    void changeItem(Item item);

    CommentDto addComment(CommentDtoRequest commentDtoRequest, Long itemId, Long authorId);
}
