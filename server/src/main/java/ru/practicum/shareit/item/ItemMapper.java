package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingDtoResponse;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class ItemMapper {
    public ItemDto mapToItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }
        return itemDto;
    }

    public List<ItemDto> mapToItemDto(List<Item> items) {
        return items.stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    public ItemDtoBooking mapToItemDtoBooking(Item item, BookingDtoResponse last,
                                                     BookingDtoResponse next, List<CommentDto> comments) {
        return new ItemDtoBooking(item.getId(), item.getName(), item.getDescription(),
                item.getAvailable(), last, next, comments);
    }

    public List<ItemDtoBooking> mapToItemsDtosBooking(List<Item> items, Map<Long, List<Comment>> comments,
                                                Map<Long, Booking> lastBookings,
                                                Map<Long, Booking> nextBookings) {
        return items.stream().map(item -> {
            BookingDtoResponse last = null;
            BookingDtoResponse next = null;
            if (lastBookings != null && !lastBookings.isEmpty()) {
                Booking lastList = lastBookings.get(item.getId());
                if (lastList != null) {
                    last = BookingMapper.mapToBookingDtoResponse(lastList);
                }
            }
            if (nextBookings != null && !nextBookings.isEmpty()) {
                Booking nextList = nextBookings.get(item.getId());
                if (nextList != null) {
                    next = BookingMapper.mapToBookingDtoResponse(nextList);
                }
            }
            List<CommentDto> commentDtoList = null;
            if (comments != null && !comments.isEmpty()) {
                List<Comment> commentList = comments.get(item.getId());
                if (commentList != null && !commentList.isEmpty()) {
                    commentDtoList = CommentMapper.mapToCommentDto(commentList);
                }
            }
            return mapToItemDtoBooking(item, last, next, commentDtoList);
        }).collect(Collectors.toList());
    }

    public Item mapToNewItem(ItemDto itemDto, User owner, ItemRequest itemRequest) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setUser(owner);
        if (itemRequest != null) {
            item.setRequest(itemRequest);
        }
        return item;
    }
}
