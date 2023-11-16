package ru.practicum.shareit.item.mem;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    List<ItemDto> findItemByUserId(Long userId);

    Optional<ItemDto> getById(Long itemId);

    ItemDto add(ItemDto itemDto);

    ItemDto update(Long userId, ItemDto itemDto);

    void deleteByUserIdAndItemId(Long userId, Long itemId);

    List<ItemDto> search(String searchText);
}
