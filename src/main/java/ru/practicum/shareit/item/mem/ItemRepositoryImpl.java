package ru.practicum.shareit.item.mem;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Set<Item>> items = new HashMap<>();
    private final Map<Long, Item> allItems = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    @Override
    public List<Item> findItemByUserId(Long userId) {
        return new ArrayList<>(items.getOrDefault(userId, Collections.emptySet()));
    }

    @Override
    public Optional<Item> getById(Long itemId) {
        return Optional.ofNullable(allItems.get(itemId));
    }

    @Override
    public Item add(Item item) {
        item.setId(idGenerator.incrementAndGet());
        items.computeIfAbsent(item.getOwner().getId(), userId -> new HashSet<>()).add(item);
        allItems.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Long userId, Item item) {
        Set<Item> userItems = items.get(userId);

        if (userItems != null) {
            if (userItems.removeIf(currentItem -> currentItem.getId().equals(item.getId()))) {
                userItems.add(item);
                allItems.put(item.getId(), item);
                return item;
            }
        }
        return null;
    }


    @Override
    public void deleteByUserIdAndItemId(Long userId, Long itemId) {
        Set<Item> userItems = items.get(userId);
        if (userItems != null) {
            userItems.removeIf(item -> item.getId().equals(itemId));
            allItems.remove(itemId);
        }
    }

    @Override
    public List<Item> search(String searchText) {
        String searchLower = searchText.toLowerCase();
        return allItems.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(searchLower) ||
                        item.getDescription().toLowerCase().contains(searchLower))
                .collect(Collectors.toList());
    }

}
