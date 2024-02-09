package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ItemRequestMapper {
    public ItemRequestDto toDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated());
    }

    public ItemRequest toModel(User user, ItemRequestDto itemRequestDto) {
        return new ItemRequest(itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                user,
                LocalDateTime.now());

    }

    public ItemRequestInfoDto toInfoDto(ItemRequest itemRequest, List<ItemDto> itemDto) {
        return new ItemRequestInfoDto(itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                itemDto);
    }
}
