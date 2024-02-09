package ru.practicum.shareit.booking.dto;


public enum BookingState {
    PAST,
    FUTURE,
    WAITING,
    REJECTED,
    CURRENT,
    ALL;

    public static BookingState fromString(String state) {
        String stateLower = state.toLowerCase();
        for (BookingState status : values()) {
            if (status.name().toLowerCase().equals(stateLower)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown state: " + state);
    }
}


