package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserBookingDto;
import ru.practicum.shareit.user.model.User;


import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookingMapper {


    public Booking toModel(BookingCreateDto bookingCreateDto, User booker, Item item) {
        return new Booking(bookingCreateDto.getId(),
                bookingCreateDto.getStart(),
                bookingCreateDto.getEnd(),
                item,
                booker,
                BookingStatus.WAITING);
    }

    public List<BookingCreateDto> toListDto(List<Booking> bookingList) {
        return bookingList.stream()
                .map(booking -> new BookingCreateDto(booking.getId(),
                        booking.getStart(),
                        booking.getEnd(),
                        booking.getStatus(),
                        booking.getItem().getId()))
                .collect(Collectors.toList());
    }

    public BookingResponseDto toDto(Booking booking) {
        return new BookingResponseDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                new UserBookingDto(booking.getBooker().getId()),
                new ItemBookingDto(booking.getItem().getId(), booking.getItem().getName()));
    }

}

