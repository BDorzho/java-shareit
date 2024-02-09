package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;


    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @RequestBody ItemRequestDto itemRequestDto) {
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
                                           @RequestParam Integer from,
                                           @RequestParam Integer size) {

        log.info("Получение списка запросов, созданных другими пользователями");
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());
        List<ItemRequestInfoDto> itemRequestDto = itemRequestService.getAll(userId, pageable);
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
