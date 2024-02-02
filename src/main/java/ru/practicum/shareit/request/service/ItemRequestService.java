package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestInfoDto> get(long userId);

    List<ItemRequestInfoDto> getAll(long userId, int from, int size);

    ItemRequestInfoDto getById(long userId, long requestId);
}
