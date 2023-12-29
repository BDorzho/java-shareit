package ru.practicum.shareit.item.mapper;


import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Component
public class ItemMapper {

    public ItemCreateDto toDto(Item item) {
        return new ItemCreateDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner() != null ? item.getOwner().getId() : null);
    }

    public Item toModel(ItemCreateDto itemCreateDto) {
        Item item = new Item();
        item.setId(itemCreateDto.getId());
        item.setName(itemCreateDto.getName());
        item.setDescription(itemCreateDto.getDescription());
        item.setAvailable(itemCreateDto.getAvailable());

        if (itemCreateDto.getOwner() != null) {
            User owner = new User();
            owner.setId(itemCreateDto.getOwner());
            item.setOwner(owner);
        }

        return item;
    }

    public List<ItemCreateDto> toListDto(List<Item> itemList) {
        List<ItemCreateDto> itemCreateDtoList = new ArrayList<>();
        for (Item item : itemList) {
            itemCreateDtoList.add(new ItemCreateDto(
                    item.getId(),
                    item.getName(),
                    item.getDescription(),
                    item.getAvailable(),
                    item.getOwner() != null ? item.getOwner().getId() : null));
        }
        return itemCreateDtoList;
    }

    public ItemInfoDto toInfoDto(Item item, Booking lastBooking, Booking nextBooking, List<CommentDto> comments) {
        return new ItemInfoDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking != null ? new BookingDto(lastBooking.getId(), lastBooking.getStart(), lastBooking.getEnd(), lastBooking.getBooker().getId()) : null,
                nextBooking != null ? new BookingDto(nextBooking.getId(), nextBooking.getStart(), nextBooking.getEnd(), nextBooking.getBooker().getId()) : null,
                comments
        );
    }


}
