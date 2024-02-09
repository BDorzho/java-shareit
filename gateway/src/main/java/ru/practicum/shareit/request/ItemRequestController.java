package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Validated
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;


    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Создание запроса на добавление вещи");
        ResponseEntity<Object> createItemRequestDto = itemRequestClient.create(userId, itemRequestDto);
        log.info("Запрос создан");
        return createItemRequestDto;
    }

    @GetMapping
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получение списка запросов");
        ResponseEntity<Object> itemRequestDto = itemRequestClient.getRequest(userId);
        log.info("Список получен");
        return itemRequestDto;

    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                         @RequestParam(value = "size", defaultValue = "20") @Min(1) @Max(100) Integer size) {

        log.info("Получение списка запросов, созданных другими пользователями");

        ResponseEntity<Object> itemRequestDto = itemRequestClient.getAll(userId, from, size);
        log.info("Список получен");
        return itemRequestDto;
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable long requestId) {
        log.info("Получение данных о конкретном запросе");
        ResponseEntity<Object> itemRequestDto = itemRequestClient.getById(userId, requestId);
        log.info("Данные получены");
        return itemRequestDto;
    }
}

