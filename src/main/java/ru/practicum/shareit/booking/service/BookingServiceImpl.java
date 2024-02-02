package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
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
    private final BookingMapper mapper;

    @Transactional
    @Override
    public BookingDto add(long bookerId, BookingCreateDto bookingCreateDto) {

        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepository.findById(bookingCreateDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (item.getOwner().getId() == bookerId) {
            throw new NotFoundException("Владелец не может забронировать свою вещь");
        }

        if (!item.getAvailable()) {
            throw new ValidationException("Выбранный предмет недоступен для бронирования");
        }

        if (bookingRepository.existsByItemIdAndEndAfterAndStartBefore(item.getId(), bookingCreateDto.getStart(), bookingCreateDto.getEnd())) {
            throw new NotFoundException("Это время уже занято для выбранной вещи");
        }

        Booking booking = mapper.toModel(bookingCreateDto, booker, item);

        Booking savedBooking = bookingRepository.save(booking);

        return mapper.toDto(savedBooking);
    }

    @Transactional
    @Override
    public BookingDto update(long userId, Long bookingId, boolean approved) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        Item item = booking.getItem();
        if (item.getOwner().getId() != userId) {
            throw new NotFoundException("Изменение статуса бронирования разрешено только владельцу");
        }
        if (approved && booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Невозможно подтвердить бронирование из-за неверного статуса");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);
        return mapper.toDto(updatedBooking);
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto get(long userId, Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        Item item = booking.getItem();
        if (booking.getBooker().getId() == userId || item.getOwner().getId() == userId) {
            return mapper.toDto(booking);
        } else {
            throw new NotFoundException("Пользователь не автор бронирования и не владелец вещи");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getBookingsForBooker(long userId, BookingState state, int from, int size) {

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<Booking> bookings = getBookingsBasedOnStateForBooker(userId, state, from, size);

        return bookings.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getBookingsForOwner(long ownerId, BookingState state, int from, int size) {

        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<Booking> bookings = getBookingsBasedOnStateForOwner(ownerId, state, from, size);

        return bookings.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }


    private List<Booking> getBookingsBasedOnStateForBooker(long userId, BookingState state, int from, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = PageRequest.of(from / size, size, sort);
        switch (state) {
            case PAST:
                return bookingRepository.findByBooker_IdAndEndIsBefore(userId, LocalDateTime.now(), pageable);
            case FUTURE:
                return bookingRepository.findByBooker_IdAndStartIsAfter(userId, LocalDateTime.now(), pageable);
            case WAITING:
                return bookingRepository.findByBooker_IdAndStatus(userId, BookingStatus.WAITING, pageable);
            case REJECTED:
                return bookingRepository.findByBooker_IdAndStatus(userId, BookingStatus.REJECTED, pageable);
            case CURRENT:
                return bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(), LocalDateTime.now(), pageable);
            case ALL:
            default:
                return bookingRepository.findByBooker_Id(userId, pageable);
        }
    }

    private List<Booking> getBookingsBasedOnStateForOwner(long ownerId, BookingState state, int from, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = PageRequest.of(from / size, size, sort);
        switch (state) {
            case PAST:
                return bookingRepository.findByItem_Owner_IdAndEndIsBefore(ownerId, LocalDateTime.now(), pageable);
            case FUTURE:
                return bookingRepository.findByItem_Owner_IdAndStartIsAfter(ownerId, LocalDateTime.now(), pageable);
            case WAITING:
                return bookingRepository.findByItem_Owner_IdAndStatus(ownerId, BookingStatus.WAITING, pageable);
            case REJECTED:
                return bookingRepository.findByItem_Owner_IdAndStatus(ownerId, BookingStatus.REJECTED, pageable);
            case CURRENT:
                return bookingRepository.findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(ownerId, LocalDateTime.now(), LocalDateTime.now(), pageable);
            case ALL:
            default:
                return bookingRepository.findByItem_Owner_Id(ownerId, pageable);
        }
    }

}


