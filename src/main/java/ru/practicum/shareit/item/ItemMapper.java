package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingDtoResponse;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemDto mapToItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable());
    }

    public static List<ItemDto> mapToItemDto(List<Item> items) {
        return items.stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    public static ItemDtoBooking mapToItemDtoBooking(Item item, BookingDtoResponse last,
                                                     BookingDtoResponse next, List<CommentDto> comments) {
        return new ItemDtoBooking(item.getId(), item.getName(), item.getDescription(),
                item.getAvailable(), last, next, comments);
    }

    public static Item mapToNewItem(ItemDto itemDto, User owner) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setUser(owner);
        return item;
    }
}
