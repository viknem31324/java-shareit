package ru.practicum.shareit.item;

import ru.practicum.shareit.user.User;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static Item toItem(ItemDto itemDto, User owner) {
        return Item.builder()
                .id(itemDto.getId())
                .owner(owner)
                .name(itemDto.getName() != null ? itemDto.getName() : null)
                .description(itemDto.getDescription() != null ? itemDto.getDescription() : null)
                .available(itemDto.getAvailable() != null ? itemDto.getAvailable() : null)
                .build();
    }
}
