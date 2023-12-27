package ru.practicum.shareit.booking.mapper;

import org.modelmapper.ModelMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;


import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {
    private static final ModelMapper modelMapper = new ModelMapper();

    public static BookingDto toBookingDto(Booking booking) {
        return modelMapper.map(booking, BookingDto.class);
    }

    public static Booking toBooking(BookingDto bookingDto) {
        return modelMapper.map(bookingDto, Booking.class);
    }

    public static List<BookingDto> toBookingDtoList(List<Booking> bookingList) {
        return bookingList.stream()
                .map(booking -> modelMapper.map(booking, BookingDto.class))
                .collect(Collectors.toList());
    }

    public static BookingResponseDto toBookingResponseDto(Booking booking) {
        return modelMapper.map(booking, BookingResponseDto.class);
    }

    public static BookingItemDto toBookingItemDto(Booking booking) {
        return modelMapper.map(booking, BookingItemDto.class);
    }
}

