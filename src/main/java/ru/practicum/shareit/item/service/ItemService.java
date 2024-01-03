package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;


import java.util.List;

public interface ItemService {
    List<ItemInfoDto> getItems(long userId);

    ItemInfoDto getById(Long itemId, Long userId);

    ItemDto add(Long userId, ItemDto itemDto);

    void delete(Long userId, Long itemId);

    ItemDto update(Long userId, ItemDto itemDto);

    List<ItemDto> search(String searchText);

    CommentDto addCommentToItem(Long itemId, Long userId, CommentDto commentDto);
}
