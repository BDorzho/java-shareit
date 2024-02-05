package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.exception.NotFoundException;
import ru.practicum.shareit.validation.exception.ValidationException;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    private final ItemMapper mapper;

    @Transactional(readOnly = true)
    @Override
    public List<ItemInfoDto> getItems(long userId, Pageable pageable) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<Item> itemPage = itemRepository.findItemsByOwnerId(userId, pageable);

        List<Long> itemIds = itemPage.stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        List<Booking> bookingList = bookingRepository.findByItems(itemIds);
        List<Comment> commentList = commentRepository.findByItems(itemIds);

        Map<Long, List<Booking>> bookingsMap = bookingList.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        Map<Long, List<Comment>> commentsMap = commentList.stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        return itemPage.stream()
                .map(item -> {
                    List<Booking> itemBookings = bookingsMap.getOrDefault(item.getId(), Collections.emptyList());
                    List<Comment> itemComments = commentsMap.getOrDefault(item.getId(), Collections.emptyList());
                    return getItemDetails(item, itemComments, itemBookings, userId);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public ItemInfoDto getById(Long itemId, long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        List<Comment> comments = commentRepository.findByItemId(itemId);
        List<Booking> bookings = bookingRepository.findByItemId(itemId);

        return getItemDetails(item, comments, bookings, userId);
    }

    @Transactional
    @Override
    public ItemDto add(long userId, ItemDto itemDto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        itemDto.setOwner(userId);
        Item item = mapper.toModel(itemDto);
        return mapper.toDto(itemRepository.save(item));
    }

    @Override
    public void delete(long userId, Long itemId) {
        itemRepository.deleteByIdAndOwnerId(itemId, userId);
    }

    @Transactional
    @Override
    public ItemDto update(long userId, ItemDto itemDto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item updateItem = itemRepository.findById(itemDto.getId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        if (updateItem.getOwner().getId() != userId) {
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
        itemRepository.save(updateItem);
        return mapper.toDto(updateItem);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> search(String searchText, Pageable pageable) {
        if (searchText == null || searchText.isEmpty()) {
            return Collections.emptyList();
        }

        List<Item> itemPage = itemRepository.search(searchText, pageable);
        return mapper.toListDto(itemPage);
    }

    @Transactional
    @Override
    public CommentDto addCommentToItem(Long itemId, long userId, CommentDto commentDto) {
        LocalDateTime now = LocalDateTime.now();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        List<Booking> booking = bookingRepository.findByItemIdAndBookerIdAndEndTimeBeforeNow(itemId, userId, now);
        if (booking.isEmpty()) {
            throw new ValidationException("Пользователь не имеет права оставлять комментарии для этой вещи");
        }
        Comment comment = commentRepository.save(CommentMapper.toModel(commentDto, item, user));

        return CommentMapper.toDto(comment);
    }

    private ItemInfoDto getItemDetails(Item item, List<Comment> comments, List<Booking> bookings, long userId) {
        LocalDateTime now = LocalDateTime.now();
        Booking lastBooking = null;
        Booking nextBooking = null;

        if (item.getOwner().getId() == userId) {
            Optional<Booking> lastBookingOptional = bookings.stream()
                    .filter(booking -> booking.getStart().isBefore(now) || booking.getEnd().isBefore(now))
                    .max(Comparator.comparing(Booking::getEnd));

            lastBooking = lastBookingOptional.orElse(null);

            if (lastBooking == null) {
                return mapper.toInfoDto(item, null, null, CommentMapper.toListDto(comments));
            }

            LocalDateTime endOfLastBooking = lastBookingOptional.get().getEnd();

            Optional<Booking> nextBookingOptional = bookings.stream()
                    .filter(booking -> booking.getStart().isAfter(endOfLastBooking))
                    .min(Comparator.comparing(Booking::getStart));

            nextBooking = nextBookingOptional.orElse(null);
        }
        return mapper.toInfoDto(item, lastBooking, nextBooking, CommentMapper.toListDto(comments));
    }
}
