package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode
public class User {
    Long id;
    String email;
    String name;
}
