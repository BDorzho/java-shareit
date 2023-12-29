package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;


import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @Valid @RequestBody BookingCreateDto bookingCreateDto) {
        log.info("Добавление бронирования");
        BookingResponseDto createBooking = bookingService.add(userId, bookingCreateDto);
        log.info("Запрос создан");
        return createBooking;
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long bookingId,
                                     @RequestParam Boolean approved) {
        log.info("Обновление бронирования: {}", bookingId);
        BookingResponseDto updatedBooking = bookingService.update(userId, bookingId, approved);
        log.info("Бронирование обновлено: {}", bookingId);
        return updatedBooking;
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto get(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long bookingId) {
        log.info("Получение данных о бронировании с id: {}", bookingId);
        BookingResponseDto bookingResponseDto = bookingService.get(userId, bookingId);
        log.info("Данные получены: {}", bookingId);
        return bookingResponseDto;
    }

    @GetMapping
    public List<BookingResponseDto> getBookingsByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestParam(required = false) String state) {

        log.info("Получение списка всех бронирований текущего пользователя: {}", userId);

        BookingState bookingState = null;
        if (state != null) {
            try {
                bookingState = BookingState.valueOf(state);

            } catch (IllegalArgumentException e) {
                log.error("Некорректное значение статуса бронирования: {}", state);
                throw new IllegalArgumentException("Unknown state: " + state, e);
            }
        }

        List<BookingResponseDto> bookings = bookingService.getBookingsForBooker(userId, bookingState);
        log.info("Данные получены, размер списка: {}", bookings.size());
        return bookings;
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @RequestParam(required = false) String state) {

        log.info("Получение списка бронирований для всех вещей владельца: {}", userId);

        BookingState bookingState = null;
        if (state != null) {
            try {
                bookingState = BookingState.valueOf(state);
            } catch (IllegalArgumentException e) {
                log.error("Некорректное значение статуса бронирования: {}", state);
                throw new IllegalArgumentException("Unknown state: " + state);
            }
        }
        List<BookingResponseDto> bookings = bookingService.getBookingsForOwner(userId, bookingState);

        log.info("Данные получены, размер списка: {}", userId);
        return bookings;
    }

}

