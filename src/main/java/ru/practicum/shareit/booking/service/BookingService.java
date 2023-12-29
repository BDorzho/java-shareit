package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto add(Long bookerId, BookingCreateDto bookingCreateDto);

    BookingResponseDto update(Long userId, Long bookingId, boolean approved);

    BookingResponseDto get(Long userId, Long bookingId);

    List<BookingResponseDto> getBookingsForBooker(Long userId, BookingState state);

    List<BookingResponseDto> getBookingsForOwner(Long userId, BookingState state);

}
