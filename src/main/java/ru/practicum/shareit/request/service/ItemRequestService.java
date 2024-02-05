package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestInfoDto> get(long userId);

    List<ItemRequestInfoDto> getAll(long userId, Pageable pageable);

    ItemRequestInfoDto getById(long userId, long requestId);
}
