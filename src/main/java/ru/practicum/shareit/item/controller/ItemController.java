package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;


import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получение списка вещей");
        List<ItemDto> items = itemService.getItems(userId);
        log.info("Получено {} вещей ", items.size());
        return items;
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@PathVariable long itemId) {
        log.info("Получение вещи с идентификатором: {}", itemId);
        ItemDto itemDto = itemService.getById(itemId);
        log.info("Вещь с идентификатором {} получен", itemId);
        return itemDto;
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") long userId,
                       @RequestBody ItemDto itemDto) {
        log.info("Пользователь с id: {}, создает вещь", userId);
        ItemDto createItem = itemService.add(userId, itemDto);
        log.info("Вещь создана");
        return createItem;
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable long itemId) {
        log.info("Удаление вещи с id: {}, у пользователя с id: {}", itemId, userId);
        itemService.delete(userId, itemId);
        log.info("Вещь с id: {}, у пользователя с id: {} удален", itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                          @PathVariable long itemId,
                          @RequestBody ItemDto itemDto) {
        log.info("Редактирование свойств вещи у пользователя с id: {}", userId);
        ItemDto updateItem = itemService.update(itemId, userId, itemDto);
        log.info("Вещь с идентификатором: {} отредактирована", itemId);
        return updateItem;
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam("text") String searchText) {
        log.info("Поиск вещей по тексту: {}", searchText);
        List<ItemDto> items = itemService.search(searchText);
        log.info("Найдено {} вещей по тексту {}", items.size(), searchText);
        return items;
    }
}

