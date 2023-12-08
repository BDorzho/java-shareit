package ru.practicum.shareit.item.mem;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    List<Item> findItemByUserId(Long userId);

    Optional<Item> getById(Long itemId);

    Item add(Item item);

    void update(Long userId, Item item);

    void deleteByUserIdAndItemId(Long userId, Long itemId);

    List<Item> search(String searchText);
}
