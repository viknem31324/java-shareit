package ru.practicum.shareit.helpers;

public abstract class Constant {
    public static final String HEADER = "X-Sharer-User-Id";

    public enum BookingState {
        ALL,
        CURRENT,
        FUTURE,
        WAITING,
        REJECTED,
        PAST
    }

    public enum BookingStatus {
        WAITING,
        APPROVED,
        REJECTED,
        CANCELED
    }
}
