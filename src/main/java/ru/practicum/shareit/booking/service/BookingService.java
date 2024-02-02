package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto add(long bookerId, BookingCreateDto bookingCreateDto);

    BookingDto update(long userId, Long bookingId, boolean approved);

    BookingDto get(long userId, Long bookingId);

    List<BookingDto> getBookingsForBooker(long userId, BookingState state, int from, int size);

    List<BookingDto> getBookingsForOwner(long userId, BookingState state, int from, int size);

}
