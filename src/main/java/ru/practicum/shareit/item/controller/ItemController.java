package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.valid.OnCreate;
import ru.practicum.shareit.validation.valid.OnUpdate;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemInfoDto> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение списка вещей");
        List<ItemInfoDto> items = itemService.getItems(userId);
        log.info("Получено {} вещей ", items.size());
        return items;
    }

    @GetMapping("/{itemId}")
    public ItemInfoDto get(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long itemId) {
        log.info("Получение вещи с идентификатором: {}", itemId);
        ItemInfoDto itemDto = itemService.getById(itemId, userId);
        log.info("Вещь с идентификатором {} получен", itemId);
        return itemDto;
    }

    @PostMapping
    public ItemCreateDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @Validated(OnCreate.class) @RequestBody ItemCreateDto itemCreateDto) {
        log.info("Пользователь с id: {}, создает вещь", userId);
        ItemCreateDto createItem = itemService.add(userId, itemCreateDto);
        log.info("Вещь создана");
        return createItem;
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long itemId) {
        log.info("Удаление вещи с id: {}, у пользователя с id: {}", itemId, userId);
        itemService.delete(userId, itemId);
        log.info("Вещь с id: {}, у пользователя с id: {} удален", itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemCreateDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                @PathVariable Long itemId,
                                @Validated(OnUpdate.class) @RequestBody ItemCreateDto itemCreateDto) {
        log.info("Редактирование свойств вещи у пользователя с id: {}", userId);
        itemCreateDto.setId(itemId);
        ItemCreateDto updateItem = itemService.update(userId, itemCreateDto);
        log.info("Вещь с идентификатором: {} отредактирована", itemId);
        return updateItem;
    }

    @GetMapping("/search")
    public List<ItemCreateDto> search(@RequestParam("text") String searchText) {
        log.info("Поиск вещей по тексту: {}", searchText);
        List<ItemCreateDto> items = itemService.search(searchText);
        log.info("Найдено {} вещей по тексту {}", items.size(), searchText);
        return items;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long itemId,
                                 @Validated(OnCreate.class) @RequestBody CommentDto commentDto) {
        log.info("Пользователь с id: {}, добавляет комментарий", userId);
        CommentDto comment = itemService.addCommentToItem(itemId, userId, commentDto);
        log.info("Комментарий успешно добавлен");
        return comment;
    }
}

