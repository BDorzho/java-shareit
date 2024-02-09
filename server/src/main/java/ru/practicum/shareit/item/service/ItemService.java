package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;


import java.util.List;

public interface ItemService {
    List<ItemInfoDto> getItems(long userId, Pageable pageable);

    ItemInfoDto getById(Long itemId, long userId);

    ItemDto add(long userId, ItemDto itemDto);

    void delete(long userId, Long itemId);

    ItemDto update(long userId, ItemDto itemDto);

    List<ItemDto> search(String searchText, Pageable pageable);

    CommentDto addCommentToItem(Long itemId, long userId, CommentDto commentDto);
}