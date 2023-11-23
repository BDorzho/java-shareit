package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mem.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mem.UserRepository;
import ru.practicum.shareit.validation.exception.NotFoundException;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    @Override
    public List<ItemDto> getItems(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return ItemMapper.toItemDtoList(itemRepository.findItemByUserId(userId));
    }

    @Override
    public ItemDto getById(Long itemId) {
        return ItemMapper.toItemDto(itemRepository.getById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена")));
    }

    @Override
    public ItemDto add(Long userId, ItemDto itemDto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        itemDto.setOwner(userId);
        Item item = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto(itemRepository.add(item));
    }

    @Override
    public void delete(Long userId, Long itemId) {
        itemRepository.deleteByUserIdAndItemId(userId, itemId);
    }

    @Override
    public ItemDto update(Long itemId, Long userId, ItemDto itemDto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item updateItem = itemRepository.getById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        if (!updateItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Вещь может редактировать только ёё владелец");
        }
        if (itemDto.getName() != null) {
            updateItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            updateItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            updateItem.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.update(userId, updateItem));
    }

    @Override
    public List<ItemDto> search(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            return Collections.emptyList();
        }
        return ItemMapper.toItemDtoList(itemRepository.search(searchText));
    }
}
