package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDetailDto;
import ru.practicum.shareit.item.dto.ItemDto;


import java.util.List;

public interface ItemService {
    List<ItemDetailDto> getItems(Long userId);

    ItemDetailDto getById(Long itemId, Long userId);

    ItemDto add(Long userId, ItemDto itemDto);

    void delete(Long userId, Long itemId);

    ItemDto update(Long itemId, Long userId, ItemDto itemDto);

    List<ItemDto> search(String searchText);

    CommentDto addCommentToItem(Long itemId, Long userId, CommentDto commentDto);
}
