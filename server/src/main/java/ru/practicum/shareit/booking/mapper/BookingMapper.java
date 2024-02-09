package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemBookingInfoDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.BookerDto;
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
                        booking.getItem().getId()))
                .collect(Collectors.toList());
    }

    public BookingDto toDto(Booking booking) {
        return new BookingDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                new BookerDto(booking.getBooker().getId()),
                new ItemBookingInfoDto(booking.getItem().getId(), booking.getItem().getName()));
    }

}

