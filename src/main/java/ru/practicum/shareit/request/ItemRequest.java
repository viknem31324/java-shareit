package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode
public class ItemRequest {
    Long id;
    String description;
    User requestor;
    LocalDate created;
}
