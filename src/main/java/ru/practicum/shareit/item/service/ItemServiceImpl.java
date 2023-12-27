package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDetailDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.exception.NotFoundException;
import ru.practicum.shareit.validation.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    @Override
    public List<ItemDetailDto> getItems(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<Item> items = itemRepository.findItemsByOwnerId(userId);
        List<ItemDetailDto> itemDtos = new ArrayList<>();

        for (Item item : items) {
            ItemDetailDto itemDetailDto = getItemDetails(item, userId);
            itemDtos.add(itemDetailDto);
        }

        return itemDtos;
    }

    @Override
    public ItemDetailDto getById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        return getItemDetails(item, userId);
    }


    @Override
    public ItemDto add(Long userId, ItemDto itemDto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        itemDto.setOwner(userId);
        Item item = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public void delete(Long userId, Long itemId) {
        itemRepository.deleteByUserIdAndItemId(userId, itemId);
    }

    @Override
    public ItemDto update(Long itemId, Long userId, ItemDto itemDto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item updateItem = itemRepository.findById(itemId)
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
        itemRepository.update(userId, itemId, updateItem.getName(), updateItem.getDescription(), updateItem.getAvailable());
        return ItemMapper.toItemDto(updateItem);
    }

    @Override
    public List<ItemDto> search(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            return Collections.emptyList();
        }
        return ItemMapper.toItemDtoList(itemRepository.search(searchText));
    }

    @Override
    public CommentDto addCommentToItem(Long itemId, Long userId, CommentDto commentDto) {
        LocalDateTime now = LocalDateTime.now();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        List<Booking> booking = bookingRepository.findBookingByItemIdAndBookerIdAndEndTimeBeforeNow(itemId, userId, now);
        if (booking.isEmpty()) {
            throw new ValidationException("Пользователь не имеет права оставлять комментарии для этой вещи");
        }
        Comment comment = commentRepository.save(CommentMapper.toComment(commentDto, item, user, now));

        return CommentMapper.toCommentDto(comment);
    }

    private ItemDetailDto getItemDetails(Item item, Long userId) {
        List<Comment> comment = commentRepository.findByItemId(item.getId());
        BookingItemDto lastBooking = null;
        BookingItemDto nextBooking = null;
        LocalDateTime now = LocalDateTime.now();
        if (item.getOwner().getId().equals(userId)) {

            List<Booking> bookings = bookingRepository.findBookingByItemId(item.getId());

            Optional<Booking> lastBookingOptional = bookings.stream()
                    .filter(booking -> booking.getStart().isBefore(now) || booking.getEnd().isBefore(now))
                    .max(Comparator.comparing(Booking::getEnd));

            lastBooking = lastBookingOptional.map(BookingMapper::toBookingItemDto).orElse(null);

            if (lastBooking == null) {
                return ItemMapper.toItemDetailDto(item, null, null, CommentMapper.toCommentDtoList(comment));
            }

            LocalDateTime endOfLastBooking = lastBookingOptional.get().getEnd();

            Optional<Booking> nextBookingOptional = bookings.stream()
                    .filter(booking -> booking.getStart().isAfter(endOfLastBooking))
                    .min(Comparator.comparing(Booking::getStart));

            nextBooking = nextBookingOptional.map(BookingMapper::toBookingItemDto).orElse(null);
        }
        return ItemMapper.toItemDetailDto(item, lastBooking, nextBooking, CommentMapper.toCommentDtoList(comment));
    }
}
