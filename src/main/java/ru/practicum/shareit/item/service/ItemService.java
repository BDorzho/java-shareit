package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;


import java.util.List;

public interface ItemService {
    List<ItemInfoDto> getItems(long userId);

    ItemInfoDto getById(Long itemId, Long userId);

    ItemCreateDto add(Long userId, ItemCreateDto itemCreateDto);

    void delete(Long userId, Long itemId);

    ItemCreateDto update(Long userId, ItemCreateDto itemCreateDto);

    List<ItemCreateDto> search(String searchText);

    CommentDto addCommentToItem(Long itemId, Long userId, CommentDto commentDto);
}
