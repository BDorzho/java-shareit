package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;


import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto add(@RequestHeader("X-Sharer-User-Id") long userId,
                          @Valid @RequestBody BookingCreateDto bookingCreateDto) {
        log.info("Добавление бронирования");
        BookingDto createBooking = bookingService.add(userId, bookingCreateDto);
        log.info("Запрос создан");
        return createBooking;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                             @PathVariable Long bookingId,
                             @RequestParam Boolean approved) {
        log.info("Обновление бронирования: {}", bookingId);
        BookingDto updatedBooking = bookingService.update(userId, bookingId, approved);
        log.info("Бронирование обновлено: {}", bookingId);
        return updatedBooking;
    }

    @GetMapping("/{bookingId}")
    public BookingDto get(@RequestHeader("X-Sharer-User-Id") long userId,
                          @PathVariable Long bookingId) {
        log.info("Получение данных о бронировании с id: {}", bookingId);
        BookingDto bookingDto = bookingService.get(userId, bookingId);
        log.info("Данные получены: {}", bookingId);
        return bookingDto;
    }

    @GetMapping
    public List<BookingDto> getBookingsByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(value = "from", defaultValue = "0") @Min(0) int from,
                                              @RequestParam(value = "size", defaultValue = "20") @Min(1) @Max(100) int size,
                                              @RequestParam(defaultValue = "ALL") String state) {

        log.info("Получение списка всех бронирований текущего пользователя: {}", userId);

        BookingState bookingState = BookingState.fromString(state);
        List<BookingDto> bookings = bookingService.getBookingsForBooker(userId, bookingState, from, size);

        log.info("Данные получены, размер списка: {}", bookings.size());
        return bookings;
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @RequestParam(value = "from", defaultValue = "0") @Min(0) int from,
                                               @RequestParam(value = "size", defaultValue = "20") @Min(1) @Max(100) int size,
                                               @RequestParam(defaultValue = "ALL") String state) {

        log.info("Получение списка бронирований для всех вещей владельца: {}", userId);

        BookingState bookingState = BookingState.fromString(state);
        List<BookingDto> bookings = bookingService.getBookingsForOwner(userId, bookingState, from, size);

        log.info("Данные получены, размер списка: {}", userId);
        return bookings;
    }


}

