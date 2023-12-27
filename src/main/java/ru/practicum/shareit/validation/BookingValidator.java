package ru.practicum.shareit.validation;

import ru.practicum.shareit.validation.exception.ValidationException;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class BookingValidator {
    public static void validateBooking(LocalDateTime start, LocalDateTime end) {
        if (!isEndTimeAfterStartTime(end, start)) {
            throw new ValidationException("Время окончания должно быть после времени начала");
        }
        if (isStartTimeInPast(start)) {
            throw new ValidationException("Время начала не должно быть в прошлом");
        }
        if (isStartTimeEqualToEndTime(start, end)) {
            throw new ValidationException("Время начала не должно быть равным времени окончания");
        }
        if (isEndTimeBeforeStartTime(end, start)) {
            throw new ValidationException("Время окончания не должно быть до времени начала");
        }
    }

    private static boolean isEndTimeAfterStartTime(LocalDateTime end, LocalDateTime start) {
        return end.isAfter(start);
    }

    private static boolean isStartTimeInPast(LocalDateTime start) {
        return start.isBefore(LocalDateTime.now(ZoneId.systemDefault()));
    }

    private static boolean isStartTimeEqualToEndTime(LocalDateTime start, LocalDateTime end) {
        return start.equals(end);
    }

    private static boolean isEndTimeBeforeStartTime(LocalDateTime end, LocalDateTime start) {
        return end.isBefore(start);
    }


}
