package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode
public class Item {
    Long id;
    User owner;
    String name;
    String description;
    Boolean available;
    ItemRequest request;
}
