package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto add(@RequestHeader("X-Sharer-User-Id") long userId,
                          @RequestBody BookingCreateDto bookingCreateDto) {
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
                                              @RequestParam Integer from,
                                              @RequestParam Integer size,
                                              @RequestParam BookingState state) {

        log.info("Получение списка всех бронирований текущего пользователя: {}", userId);

        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));

        List<BookingDto> bookings = bookingService.getBookingsForBooker(userId, state, pageable);

        log.info("Данные получены, размер списка: {}", bookings.size());
        return bookings;
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @RequestParam Integer from,
                                               @RequestParam Integer size,
                                               @RequestParam BookingState state) {

        log.info("Получение списка бронирований для всех вещей владельца: {}", userId);

        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));

        List<BookingDto> bookings = bookingService.getBookingsForOwner(userId, state, pageable);

        log.info("Данные получены, размер списка: {}", userId);
        return bookings;
    }


}

