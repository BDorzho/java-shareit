package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingItemInfoDto;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemInfoDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingItemInfoDto lastBooking;
    private BookingItemInfoDto nextBooking;
    private List<CommentDto> comments;

}
