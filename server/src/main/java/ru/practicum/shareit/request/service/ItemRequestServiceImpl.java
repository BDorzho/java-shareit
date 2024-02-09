package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.exception.NotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final ItemRequestMapper mapper;

    private final ItemMapper itemMapper;

    @Transactional
    @Override
    public ItemRequestDto create(long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        ItemRequest itemRequest = itemRequestRepository.save(mapper.toModel(user, itemRequestDto));
        return mapper.toDto(itemRequest);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestInfoDto> get(long userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<ItemRequest> userRequests = itemRequestRepository.findByRequesterIdOrderByCreatedDesc(userId);


        List<Long> requestIds = userRequests.stream().map(ItemRequest::getId).collect(Collectors.toList());


        List<Item> responseItems = itemRepository.findByRequestIdIn(requestIds);


        Map<Long, List<Item>> responseItemsPerRequest = responseItems.stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));


        return userRequests.stream().map(request -> {
            return mapper.toInfoDto(request,
                    responseItemsPerRequest.getOrDefault(request.getId(), Collections.emptyList())
                            .stream().map(itemMapper::toDto).collect(Collectors.toList())
            );
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestInfoDto> getAll(long userId, Pageable pageable) {

        List<ItemRequest> page = itemRequestRepository.findByRequesterIdNot(userId, pageable);

        List<Long> requestIds = page.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        Map<Long, List<Item>> responseItemsPerRequest = itemRepository.findByRequestIdIn(requestIds)
                .stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        return page.stream()
                .map(request -> {
                    List<ItemDto> responseItemDto = responseItemsPerRequest.getOrDefault(request.getId(), Collections.emptyList())
                            .stream()
                            .map(itemMapper::toDto)
                            .collect(Collectors.toList());
                    return mapper.toInfoDto(request, responseItemDto);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequestInfoDto getById(long userId, long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));

        List<Item> responseItems = itemRepository.findByRequestId(requestId);

        List<ItemDto> responseItemDto = responseItems.stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());

        return mapper.toInfoDto(request, responseItemDto);
    }
}
