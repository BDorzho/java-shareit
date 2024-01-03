package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto add(Long bookerId, BookingCreateDto bookingCreateDto);

    BookingDto update(Long userId, Long bookingId, boolean approved);

    BookingDto get(Long userId, Long bookingId);

    List<BookingDto> getBookingsForBooker(Long userId, BookingState state);

    List<BookingDto> getBookingsForOwner(Long userId, BookingState state);

}
