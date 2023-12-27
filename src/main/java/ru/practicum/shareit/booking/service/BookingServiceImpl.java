package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.BookingValidator;
import ru.practicum.shareit.validation.exception.AccessDeniedException;
import ru.practicum.shareit.validation.exception.NotFoundException;
import ru.practicum.shareit.validation.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    @Override
    public BookingResponseDto add(Long bookerId, BookingDto bookingDto) {

        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (item.getOwner().getId().equals(bookerId)) {
            throw new NotFoundException("Владелец не может забронировать свою вещь");
        }

        if (!item.getAvailable()) {
            throw new ValidationException("Выбранный предмет недоступен для бронирования");
        }

        BookingValidator.validateBooking(bookingDto.getStart(), bookingDto.getEnd());

        if (bookingRepository.existsByItemIdAndEndAfterAndStartBefore(item.getId(), bookingDto.getStart(), bookingDto.getEnd())) {
            throw new NotFoundException("Это время уже занято для выбранной вещи");
        }

        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        Booking savedBooking = bookingRepository.save(booking);

        return BookingMapper.toBookingResponseDto(savedBooking);
    }


    @Override
    public BookingResponseDto update(Long userId, Long bookingId, boolean approved) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        Item item = booking.getItem();
        if (!item.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Изменение статуса бронирования разрешено только владельцу");
        }
        if (approved && booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Невозможно подтвердить бронирование из-за неверного статуса");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingResponseDto(updatedBooking);
    }

    @Override
    public BookingResponseDto get(Long userId, Long bookingId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        Item item = booking.getItem();
        if (booking.getBooker().getId().equals(userId) || item.getOwner().getId().equals(userId)) {
            return BookingMapper.toBookingResponseDto(booking);
        } else {
            throw new AccessDeniedException("Пользователь не автор бронирования");
        }
    }

    @Override
    public List<BookingResponseDto> getBookingsForBooker(Long userId, BookingState state) {
        final LocalDateTime now = LocalDateTime.now();
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        List<Booking> bookings;
        if (state == null) {
            bookings = bookingRepository.findBookingsByBookerId(userId);
        } else {
            bookings = getBookingsBasedOnStateForBooker(userId, state, now);
        }

        return bookings.stream()
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getBookingsForOwner(Long ownerId, BookingState state) {
        final LocalDateTime now = LocalDateTime.now();
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        List<Booking> bookings;
        if (state == null) {
            bookings = bookingRepository.findBookingsByOwnerId(ownerId);
        } else {
            bookings = getBookingsBasedOnStateForOwner(ownerId, state, now);
        }
        return bookings.stream()
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }


    private List<Booking> getBookingsBasedOnStateForBooker(Long userId, BookingState state, LocalDateTime now) {
        switch (state) {
            case PAST:
                return bookingRepository.findBookingsByBookerIdAndEndDateBefore(userId, now);
            case FUTURE:
                return bookingRepository.findBookingsByBookerIdAndStartDateAfter(userId, now);
            case WAITING:
                return bookingRepository.findBookingsByBookerIdAndStatus(userId, BookingStatus.WAITING);
            case REJECTED:
                return bookingRepository.findBookingsByBookerIdAndStatus(userId, BookingStatus.REJECTED);
            case CURRENT:
                return bookingRepository.findBookingsByBookerIdAndStartBeforeAndEndAfter(userId, now);
            case ALL:
            default:
                return bookingRepository.findBookingsByBookerId(userId);
        }
    }

    private List<Booking> getBookingsBasedOnStateForOwner(Long userId, BookingState state, LocalDateTime now) {
        switch (state) {
            case PAST:
                return bookingRepository.findBookingsByOwnerIdAndEndDateBefore(userId, now);
            case FUTURE:
                return bookingRepository.findBookingsByOwnerIdAndStartDateAfter(userId, now);
            case WAITING:
                return bookingRepository.findBookingsByOwnerIdAndStatus(userId, BookingStatus.WAITING);
            case REJECTED:
                return bookingRepository.findBookingsByOwnerIdAndStatus(userId, BookingStatus.REJECTED);
            case CURRENT:
                return bookingRepository.findBookingsByOwnerIdAndStartBeforeAndEndAfter(userId, now);
            case ALL:
            default:
                return bookingRepository.findBookingsByOwnerId(userId);
        }
    }

}


