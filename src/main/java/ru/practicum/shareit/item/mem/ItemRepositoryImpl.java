package ru.practicum.shareit.item.mem;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, List<ItemDto>> items = new HashMap<>();

    @Override
    public List<ItemDto> findItemByUserId(Long userId) {
        return items.getOrDefault(userId, Collections.emptyList());
    }

    @Override
    public Optional<ItemDto> getById(Long itemId) {
        return items.values().stream()
                .flatMap(List::stream)
                .filter(item -> item.getId().equals(itemId))
                .findFirst();
    }

    @Override
    public ItemDto add(ItemDto itemDto) {
        itemDto.setId(getId());
        items.compute(itemDto.getOwner(), (userId, userItems) -> {
            if (userItems == null) {
                userItems = new ArrayList<>();
            }
            userItems.add(itemDto);
            return userItems;
        });

        return itemDto;
    }

    @Override
    public ItemDto update(Long userId, ItemDto item) {
        List<ItemDto> userItems = items.get(userId);

        if (userItems != null) {
            for (ItemDto currentItem : userItems) {
                if (currentItem.getId().equals(item.getId())) {
                    userItems.set(userItems.indexOf(currentItem), item);
                    items.put(userId, userItems);
                    return item;
                }
            }
        }
        return null;
    }


    @Override
    public void deleteByUserIdAndItemId(Long userId, Long itemId) {
        if (items.containsKey(userId)) {
            List<ItemDto> userItems = items.get(userId);
            userItems.removeIf(item -> item.getId().equals(itemId));
        }
    }

    @Override
    public List<ItemDto> search(String searchText) {
        return items.values().stream()
                .flatMap(List::stream)
                .filter(itemDto ->
                        itemDto.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                                itemDto.getDescription().toLowerCase().contains(searchText.toLowerCase())
                )
                .filter(ItemDto::getAvailable)
                .collect(Collectors.toList());
    }

    private long getId() {
        long lastId = items.values()
                .stream()
                .flatMap(Collection::stream)
                .mapToLong(ItemDto::getId)
                .max()
                .orElse(0);
        return lastId + 1;
    }
}
