package ru.practicum.shareit.request;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class ItemRequesMapper {
    public ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(),
                itemRequest.getRequestor().getId(), itemRequest.getCreated());
    }

    public ItemRequest mapToNewItemRequest(ItemRequestDto itemRequestDto, User requestor) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequest;
    }

    public List<ItemRequestDtoResponse> mapToItemRequestDtoResponse(List<ItemRequest> itemRequests,
                                                                    Map<Long, List<Item>> itemsMap) {
        return itemRequests.stream().map((item) -> {
            Long idItemRequest = item.getId();
            List<Item> items = itemsMap.get(idItemRequest);
            List<ItemDto> itemDtos = Collections.emptyList();
            if (items != null && !items.isEmpty()) {
                itemDtos = ItemMapper.mapToItemDto(items);
            }
            return new ItemRequestDtoResponse(item.getId(), item.getDescription(), item.getCreated(), itemDtos);
        }).collect(Collectors.toList());
    }
}
