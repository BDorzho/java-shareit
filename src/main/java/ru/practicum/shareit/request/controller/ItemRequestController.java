package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;


    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Создание запроса на добавление вещи");
        ItemRequestDto createItemRequestDto = itemRequestService.create(userId, itemRequestDto);
        log.info("Запрос создан");
        return createItemRequestDto;
    }

    @GetMapping
    public List<ItemRequestInfoDto> get(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получение списка запросов");
        List<ItemRequestInfoDto> itemRequestDto = itemRequestService.get(userId);
        log.info("Список получен");
        return itemRequestDto;

    }

    @GetMapping("/all")
    public List<ItemRequestInfoDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestParam(value = "from", defaultValue = "0") @Min(0) int from,
                                           @RequestParam(value = "size", defaultValue = "20") @Min(1) @Max(100) int size) {

        log.info("Получение списка запросов, созданных другими пользователями");
        List<ItemRequestInfoDto> itemRequestDto = itemRequestService.getAll(userId, from, size);
        log.info("Список получен");
        return itemRequestDto;
    }

    @GetMapping("/{requestId}")
    public ItemRequestInfoDto getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @PathVariable long requestId) {
        log.info("Получение данных о конкретном запросе");
        ItemRequestInfoDto itemRequestDto = itemRequestService.getById(userId, requestId);
        log.info("Данные получены");
        return itemRequestDto;
    }
}

