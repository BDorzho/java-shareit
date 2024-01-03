package ru.practicum.shareit.booking;

import java.util.HashMap;
import java.util.Map;

public enum BookingState {
    PAST,
    FUTURE,
    WAITING,
    REJECTED,
    CURRENT,
    ALL;

    private static final Map<String, BookingState> STATE_MAP = new HashMap<>();

    static {
        for (BookingState status : BookingState.values()) {
            STATE_MAP.put(status.name().toLowerCase(), status);
        }
    }

    public static BookingState fromString(String state) {
        String stateLower = state.toLowerCase();
        if (STATE_MAP.containsKey(stateLower)) {
            return STATE_MAP.get(stateLower);
        }
        throw new IllegalArgumentException("Unknown state: " + state);
    }
}
