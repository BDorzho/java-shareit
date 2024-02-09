package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation.OnCreate;
import ru.practicum.shareit.validation.OnUpdate;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                           @RequestParam(value = "size", defaultValue = "20") @Min(1) @Max(100) Integer size) {

        log.info("Получение списка вещей");

        return itemClient.getItems(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @PathVariable Long itemId) {
        log.info("Получение вещи с идентификатором: {}", itemId);
        ResponseEntity<Object> itemDto = itemClient.getById(itemId, userId);
        log.info("Вещь с идентификатором {} получен", itemId);
        return itemDto;
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @Validated(OnCreate.class) @RequestBody ItemDto itemDto) {
        log.info("Пользователь с id: {}, создает вещь", userId);
        ResponseEntity<Object> createItem = itemClient.add(userId, itemDto);
        log.info("Вещь создана");
        return createItem;
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable Long itemId) {
        log.info("Удаление вещи с id: {}, у пользователя с id: {}", itemId, userId);
        itemClient.deleteItem(userId, itemId);
        log.info("Вещь с id: {}, у пользователя с id: {} удален", itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable Long itemId,
                                         @Validated(OnUpdate.class) @RequestBody ItemDto itemDto) {
        log.info("Редактирование свойств вещи у пользователя с id: {}", userId);
        ResponseEntity<Object> updateItem = itemClient.update(itemId, userId, itemDto);
        log.info("Вещь с идентификатором: {} отредактирована", itemId);
        return updateItem;
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam("text") String searchText,
                                         @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                         @RequestParam(value = "size", defaultValue = "20") @Min(1) @Max(100) Integer size) {

        log.info("Поиск вещей по тексту: {}", searchText);

        return itemClient.search(searchText, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long itemId,
                                             @Validated(OnCreate.class) @RequestBody CommentDto commentDto) {
        log.info("Пользователь с id: {}, добавляет комментарий", userId);
        ResponseEntity<Object> comment = itemClient.addCommentToItem(itemId, userId, commentDto);
        log.info("Комментарий успешно добавлен");
        return comment;
    }
}

