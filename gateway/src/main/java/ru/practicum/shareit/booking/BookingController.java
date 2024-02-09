package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Validated
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @Valid @RequestBody BookingCreateDto bookingCreateDto) {
        log.info("Добавление бронирования");
        ResponseEntity<Object> createBooking = bookingClient.add(userId, bookingCreateDto);
        log.info("Запрос создан");
        return createBooking;
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable Long bookingId,
                                         @RequestParam Boolean approved) {
        log.info("Обновление бронирования: {}", bookingId);
        ResponseEntity<Object> updatedBooking = bookingClient.update(userId, bookingId, approved);
        log.info("Бронирование обновлено: {}", bookingId);
        return updatedBooking;
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @PathVariable Long bookingId) {
        log.info("Получение данных о бронировании с id: {}", bookingId);
        ResponseEntity<Object> bookingDto = bookingClient.getBooking(userId, bookingId);
        log.info("Данные получены: {}", bookingId);
        return bookingDto;
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                                    @RequestParam(value = "size", defaultValue = "20") @Min(1) @Max(100) Integer size,
                                                    @RequestParam(defaultValue = "ALL") String state) {

        log.info("Получение списка всех бронирований текущего пользователя: {}", userId);

        BookingState bookingState = BookingState.fromString(state);


        return bookingClient.getBookings(userId, bookingState, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                                     @RequestParam(value = "size", defaultValue = "20") @Min(1) @Max(100) Integer size,
                                                     @RequestParam(defaultValue = "ALL") String state) {

        log.info("Получение списка бронирований для всех вещей владельца: {}", userId);

        BookingState bookingState = BookingState.fromString(state);


        return bookingClient.getBookingsForOwner(userId, bookingState, from, size);
    }


}

