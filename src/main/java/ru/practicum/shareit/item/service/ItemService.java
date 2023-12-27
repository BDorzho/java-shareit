package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;


import java.util.List;

public interface ItemService {
    List<ItemDto> getItems(Long userId);

    ItemDto getById(Long itemId);

    ItemDto add(Long userId, ItemDto itemDto);

    void delete(Long userId, Long itemId);

    ItemDto update(Long itemId, Long userId, ItemDto itemDto);

    List<ItemDto> search(String searchText);

}
