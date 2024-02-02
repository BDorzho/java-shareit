package ru.practicum.shareit.item.mapper;


import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingItemInfoDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Component
public class ItemMapper {

    public ItemDto toDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner().getId(),
                (item.getRequest() != null) ? item.getRequest().getId() : null
        );
    }


    public Item toModel(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());

        User owner = new User();
        owner.setId(itemDto.getOwner());
        item.setOwner(owner);
        if (itemDto.getRequestId() != null) {
            ItemRequest request = new ItemRequest();
            request.setId(itemDto.getRequestId());
            item.setRequest(request);
        }
        return item;
    }

    public List<ItemDto> toListDto(List<Item> itemList) {
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : itemList) {
            itemDtoList.add(new ItemDto(
                    item.getId(),
                    item.getName(),
                    item.getDescription(),
                    item.getAvailable(),
                    item.getOwner().getId(),
                    (item.getRequest() != null) ? item.getRequest().getId() : null
            ));
        }
        return itemDtoList;
    }

    public ItemInfoDto toInfoDto(Item item, Booking lastBooking, Booking nextBooking, List<CommentDto> comments) {
        return new ItemInfoDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking != null ? new BookingItemInfoDto(lastBooking.getId(),
                        lastBooking.getBooker().getId()) : null,
                nextBooking != null ? new BookingItemInfoDto(nextBooking.getId(),
                        nextBooking.getBooker().getId()) : null,
                comments
        );
    }


}
