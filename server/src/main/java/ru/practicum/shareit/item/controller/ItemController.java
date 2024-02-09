package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemInfoDto> getItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @RequestParam Integer from,
                                      @RequestParam Integer size) {

        log.info("Получение списка вещей");
        Pageable pageable = PageRequest.of(from / size, size);
        List<ItemInfoDto> items = itemService.getItems(userId, pageable);
        log.info("Получено {} вещей ", items.size());
        return items;
    }

    @GetMapping("/{itemId}")
    public ItemInfoDto get(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable Long itemId) {
        log.info("Получение вещи с идентификатором: {}", itemId);
        ItemInfoDto itemDto = itemService.getById(itemId, userId);
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
                           @PathVariable Long itemId) {
        log.info("Удаление вещи с id: {}, у пользователя с id: {}", itemId, userId);
        itemService.delete(userId, itemId);
        log.info("Вещь с id: {}, у пользователя с id: {} удален", itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto itemDto) {
        log.info("Редактирование свойств вещи у пользователя с id: {}", userId);
        itemDto.setId(itemId);
        ItemDto updateItem = itemService.update(userId, itemDto);
        log.info("Вещь с идентификатором: {} отредактирована", itemId);
        return updateItem;
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam("text") String searchText,
                                @RequestParam Integer from,
                                @RequestParam Integer size) {

        log.info("Поиск вещей по тексту: {}", searchText);
        Pageable pageable = PageRequest.of(from / size, size);
        List<ItemDto> items = itemService.search(searchText, pageable);
        log.info("Найдено {} вещей по тексту {}", items.size(), searchText);
        return items;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody CommentDto commentDto) {
        log.info("Пользователь с id: {}, добавляет комментарий", userId);
        CommentDto comment = itemService.addCommentToItem(itemId, userId, commentDto);
        log.info("Комментарий успешно добавлен");
        return comment;
    }
}

