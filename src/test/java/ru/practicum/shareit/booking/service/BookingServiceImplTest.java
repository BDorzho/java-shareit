package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.exception.NotFoundException;
import ru.practicum.shareit.validation.exception.ValidationException;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplTest {

    private final BookingService bookingService;

    private final UserService userService;

    private final ItemService itemService;

    UserDto ownerItem;

    UserDto booker;

    ItemDto item;

    @BeforeEach
    void setUp() {
        ownerItem = new UserDto();
        ownerItem.setName("owner item");
        ownerItem.setEmail("owner@email.ru");

        booker = new UserDto();
        booker.setName("test booker");
        booker.setEmail("test@email.ru");

        item = new ItemDto();
        item.setName("Item 1");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(ownerItem.getId());
    }

    @Test
    public void testAddBooking() {
        // given

        UserDto expectedOwner = userService.create(ownerItem);

        UserDto expectedBooker = userService.create(booker);

        ItemDto expectedItem = itemService.add(expectedOwner.getId(), item);

        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(expectedItem.getId());
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));

        // when
        BookingDto bookingDto = bookingService.add(expectedBooker.getId(), bookingCreateDto);

        // then
        assertNotNull(bookingDto);
        assertEquals(expectedBooker.getId(), bookingDto.getBooker().getId());
        assertEquals(expectedItem.getId(), bookingDto.getItem().getId());
        assertNotNull(bookingDto.getId());
    }

    @Test
    public void testAddBookingWithInvalidBookerId() {
        // given
        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(1L);
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));

        // when
        Exception exception = assertThrows(NotFoundException.class, () -> bookingService.add(999L, bookingCreateDto));

        // then
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    public void testAddBookingWithInvalidItemId() {
        // given
        UserDto expectedBooker = userService.create(booker);

        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(999L);
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));

        // when
        Exception exception = assertThrows(NotFoundException.class, () -> bookingService.add(expectedBooker.getId(), bookingCreateDto));

        // then
        assertEquals("Вещь не найдена", exception.getMessage());
    }

    @Test
    public void testOwnerBookingOwnItem() {
        // given

        UserDto expectedOwner = userService.create(ownerItem);

        ItemDto expectedItem = itemService.add(expectedOwner.getId(), item);

        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(expectedItem.getId());
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));

        // when
        Exception exception = assertThrows(NotFoundException.class, () -> bookingService.add(expectedOwner.getId(), bookingCreateDto));

        // then
        assertEquals("Владелец не может забронировать свою вещь", exception.getMessage());

    }

    @Test
    public void testAddBookingWithUnavailableItem() {
        // given

        UserDto expectedOwner = userService.create(ownerItem);

        UserDto expectedBooker = userService.create(booker);

        item.setAvailable(false);
        ItemDto expectedItem = itemService.add(expectedOwner.getId(), item);

        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(expectedItem.getId());
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));

        // when
        Exception exception = assertThrows(ValidationException.class, () -> bookingService.add(expectedBooker.getId(), bookingCreateDto));

        // then
        assertEquals("Выбранный предмет недоступен для бронирования", exception.getMessage());

    }

    @Test
    public void testAddBookingWithBookingConflict() {
        // given

        UserDto existingOwner = userService.create(ownerItem);

        UserDto existingBooker = userService.create(booker);

        UserDto newBooker = new UserDto();
        newBooker.setName("new booker");
        newBooker.setEmail("new@email.ru");

        UserDto expectedBooker = userService.create(newBooker);


        ItemDto existingItem = itemService.add(existingOwner.getId(), item);


        BookingCreateDto existingBooking = new BookingCreateDto();
        existingBooking.setItemId(existingItem.getId());
        existingBooking.setStart(LocalDateTime.now().plusDays(1));
        existingBooking.setEnd(LocalDateTime.now().plusDays(2));

        bookingService.add(existingBooker.getId(), existingBooking);

        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(existingItem.getId());
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));

        // when
        Exception exception = assertThrows(NotFoundException.class, () -> bookingService.add(expectedBooker.getId(), bookingCreateDto));

        // then
        assertEquals("Это время уже занято для выбранной вещи", exception.getMessage());
    }

    @Test
    public void testUpdateBookingStatusToApproved() {
        // given
        UserDto expectedOwner = userService.create(ownerItem);

        UserDto expectedBooker = userService.create(booker);

        ItemDto expectedItem = itemService.add(expectedOwner.getId(), item);

        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(expectedItem.getId());
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto expectedBooking = bookingService.add(expectedBooker.getId(), bookingCreateDto);

        // when
        BookingDto updatedBookingDto = bookingService.update(expectedOwner.getId(), expectedBooking.getId(), true);

        // then
        assertNotNull(updatedBookingDto);
        assertEquals(BookingStatus.APPROVED, updatedBookingDto.getStatus());
    }

    @Test
    public void testUpdateBookingStatusToRejected() {
        // given
        UserDto expectedOwner = userService.create(ownerItem);

        UserDto expectedBooker = userService.create(booker);

        ItemDto expectedItem = itemService.add(expectedOwner.getId(), item);

        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(expectedItem.getId());
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto expectedBooking = bookingService.add(expectedBooker.getId(), bookingCreateDto);

        // when
        BookingDto updatedBookingDto = bookingService.update(expectedOwner.getId(), expectedBooking.getId(), false);

        // then
        assertNotNull(updatedBookingDto);
        assertEquals(BookingStatus.REJECTED, updatedBookingDto.getStatus());
    }

    @Test
    public void testUpdateBookingStatusWithInvalidBookingId() {
        // when
        Exception exception = assertThrows(NotFoundException.class, () -> bookingService.update(1, 999L, true));

        // then
        assertEquals("Бронирование не найдено", exception.getMessage());

    }

    @Test
    public void testUpdateBookingStatusWithNonOwnerUser() {
        // given
        UserDto actualOwner = userService.create(ownerItem);

        UserDto otherOwner = new UserDto();
        otherOwner.setName("new user");
        otherOwner.setEmail("new@email.ru");

        UserDto nonOwner = userService.create(otherOwner);

        UserDto expectedBooker = userService.create(booker);

        ItemDto expectedItem = itemService.add(actualOwner.getId(), item);

        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(expectedItem.getId());
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto expectedBooking = bookingService.add(expectedBooker.getId(), bookingCreateDto);

        // when
        Exception exception = assertThrows(NotFoundException.class, () -> bookingService.update(nonOwner.getId(), expectedBooking.getId(), true));

        // then
        assertEquals("Изменение статуса бронирования разрешено только владельцу", exception.getMessage());

    }

    @Test
    public void testUpdateBookingStatusWithInvalidStatus() {
        // given
        UserDto expectedOwner = userService.create(ownerItem);

        UserDto expectedBooker = userService.create(booker);

        ItemDto expectedItem = itemService.add(expectedOwner.getId(), item);

        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(expectedItem.getId());
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto expectedBooking = bookingService.add(expectedBooker.getId(), bookingCreateDto);

        BookingDto actualBooking = bookingService.update(expectedOwner.getId(), expectedBooking.getId(), true);

        assertEquals(BookingStatus.APPROVED, actualBooking.getStatus());

        // when
        Exception exception = assertThrows(ValidationException.class, () -> bookingService.update(expectedOwner.getId(), expectedBooking.getId(), true));

        // then
        assertEquals("Невозможно подтвердить бронирование из-за неверного статуса", exception.getMessage());
    }

    @Test
    public void testGetBooking() {
        // given
        UserDto expectedOwner = userService.create(ownerItem);

        UserDto expectedBooker = userService.create(booker);

        ItemDto expectedItem = itemService.add(expectedOwner.getId(), item);

        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(expectedItem.getId());
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto expectedBooking = bookingService.add(expectedBooker.getId(), bookingCreateDto);

        // when
        BookingDto result = bookingService.get(expectedBooker.getId(), expectedBooking.getId());

        // then
        assertNotNull(result);
        assertEquals(expectedBooking.getId(), result.getId());
        assertEquals(expectedBooker.getId(), result.getBooker().getId());
        assertEquals(expectedItem.getId(), result.getItem().getId());
    }

    @Test
    public void testGetBooking_NotFound() {
        // when
        Exception exception = assertThrows(NotFoundException.class, () -> bookingService.get(1, 1L));

        // then
        assertEquals("Бронирование не найдено", exception.getMessage());

    }

    @Test
    public void testGetBooking_NonOwnerAndBooker() {
        // given
        UserDto actualOwner = userService.create(ownerItem);

        UserDto otherOwner = new UserDto();
        otherOwner.setName("new user");
        otherOwner.setEmail("new@email.ru");

        UserDto nonOwner = userService.create(otherOwner);

        UserDto expectedBooker = userService.create(booker);

        ItemDto expectedItem = itemService.add(actualOwner.getId(), item);

        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(expectedItem.getId());
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto expectedBooking = bookingService.add(expectedBooker.getId(), bookingCreateDto);

        // when
        Exception exception = assertThrows(NotFoundException.class, () -> bookingService.get(nonOwner.getId(), expectedBooking.getId()));

        // then
        assertEquals("Пользователь не автор бронирования и не владелец вещи", exception.getMessage());
    }

    @Test
    public void testGetAllBookingForOwner() {
        // given
        UserDto expectedOwner = userService.create(ownerItem);

        UserDto expectedBooker = userService.create(booker);

        ItemDto expectedItem = itemService.add(expectedOwner.getId(), item);

        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(expectedItem.getId());
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));

        bookingService.add(expectedBooker.getId(), bookingCreateDto);

        // when

        List<BookingDto> expectedList = bookingService.getBookingsForOwner(expectedOwner.getId(), BookingState.ALL, 0, 20);

        // then

        assertEquals(expectedList.size(), 1);
        assertEquals(expectedList.get(0).getItem().getId(), expectedItem.getId());
        assertEquals(expectedList.get(0).getBooker().getId(), expectedBooker.getId());
    }

    @Test
    public void testGetOwnerBookingsSortedByDateDescending() {
        // given
        UserDto expectedOwner = userService.create(ownerItem);

        UserDto expectedBooker = userService.create(booker);

        ItemDto expectedItem = itemService.add(expectedOwner.getId(), item);

        BookingCreateDto futureBooking = new BookingCreateDto();
        futureBooking.setItemId(expectedItem.getId());
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));

        bookingService.add(expectedBooker.getId(), futureBooking);

        BookingCreateDto pastBooking = new BookingCreateDto();
        pastBooking.setItemId(expectedItem.getId());
        pastBooking.setStart(LocalDateTime.now().minusDays(5));
        pastBooking.setEnd(LocalDateTime.now().minusDays(3));

        bookingService.add(expectedBooker.getId(), pastBooking);

        // when

        List<BookingDto> expectedList = bookingService.getBookingsForOwner(expectedOwner.getId(), BookingState.ALL, 0, 20);

        // then

        assertEquals(expectedList.size(), 2);
        assertEquals(expectedList.get(0).getStart(), futureBooking.getStart());
        assertEquals(expectedList.get(1).getStart(), pastBooking.getStart());
    }

    @Test
    public void testGetPastBookingForOwner() {
        // given
        UserDto expectedOwner = userService.create(ownerItem);

        UserDto expectedBooker = userService.create(booker);

        ItemDto expectedItem = itemService.add(expectedOwner.getId(), item);

        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(expectedItem.getId());
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));

        bookingService.add(expectedBooker.getId(), bookingCreateDto);

        BookingCreateDto pastBooking = new BookingCreateDto();
        pastBooking.setItemId(expectedItem.getId());
        pastBooking.setStart(LocalDateTime.now().minusDays(5));
        pastBooking.setEnd(LocalDateTime.now().minusDays(3));

        bookingService.add(expectedBooker.getId(), pastBooking);

        // when

        List<BookingDto> expectedList = bookingService.getBookingsForOwner(expectedOwner.getId(), BookingState.PAST, 0, 20);

        // then

        assertEquals(expectedList.size(), 1);
        assertEquals(expectedList.get(0).getItem().getId(), expectedItem.getId());
        assertEquals(expectedList.get(0).getBooker().getId(), expectedBooker.getId());
        assertEquals(expectedList.get(0).getStart(), pastBooking.getStart());
        assertEquals(expectedList.get(0).getEnd(), pastBooking.getEnd());
    }

    @Test
    public void testGetFutureBookingForOwner() {
        // given
        UserDto expectedOwner = userService.create(ownerItem);

        UserDto expectedBooker = userService.create(booker);

        ItemDto expectedItem = itemService.add(expectedOwner.getId(), item);

        BookingCreateDto futureBooking = new BookingCreateDto();
        futureBooking.setItemId(expectedItem.getId());
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));

        bookingService.add(expectedBooker.getId(), futureBooking);

        BookingCreateDto pastBooking = new BookingCreateDto();
        pastBooking.setItemId(expectedItem.getId());
        pastBooking.setStart(LocalDateTime.now().minusDays(5));
        pastBooking.setEnd(LocalDateTime.now().minusDays(3));

        bookingService.add(expectedBooker.getId(), pastBooking);

        // when

        List<BookingDto> expectedList = bookingService.getBookingsForOwner(expectedOwner.getId(), BookingState.FUTURE, 0, 20);

        // then

        assertEquals(expectedList.size(), 1);
        assertEquals(expectedList.get(0).getItem().getId(), expectedItem.getId());
        assertEquals(expectedList.get(0).getBooker().getId(), expectedBooker.getId());
        assertEquals(expectedList.get(0).getStart(), futureBooking.getStart());
        assertEquals(expectedList.get(0).getEnd(), futureBooking.getEnd());
    }

    @Test
    public void testGetWaitingBookingForOwner() {
        // given
        UserDto expectedOwner = userService.create(ownerItem);

        UserDto expectedBooker = userService.create(booker);

        ItemDto expectedItem = itemService.add(expectedOwner.getId(), item);

        BookingCreateDto futureBooking = new BookingCreateDto();
        futureBooking.setItemId(expectedItem.getId());
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto futureBookingDto = bookingService.add(expectedBooker.getId(), futureBooking);

        BookingCreateDto pastBooking = new BookingCreateDto();
        pastBooking.setItemId(expectedItem.getId());
        pastBooking.setStart(LocalDateTime.now().minusDays(5));
        pastBooking.setEnd(LocalDateTime.now().minusDays(3));

        BookingDto pastBookingDto = bookingService.add(expectedBooker.getId(), pastBooking);

        // when

        List<BookingDto> expectedList = bookingService.getBookingsForOwner(expectedOwner.getId(), BookingState.WAITING, 0, 20);

        // then

        assertEquals(expectedList.size(), 2);
        assertEquals(expectedList.get(0).getItem().getId(), expectedItem.getId());
        assertEquals(expectedList.get(0).getBooker().getId(), expectedBooker.getId());
        assertEquals(expectedList.get(1).getItem().getId(), expectedItem.getId());
        assertEquals(expectedList.get(1).getBooker().getId(), expectedBooker.getId());
        assertEquals(expectedList.get(0).getStatus(), BookingStatus.WAITING);
        assertEquals(expectedList.get(0).getStatus(), futureBookingDto.getStatus());
        assertEquals(expectedList.get(1).getStatus(), BookingStatus.WAITING);
        assertEquals(expectedList.get(1).getStatus(), pastBookingDto.getStatus());

    }

    @Test
    public void testGetRejectedBookingForOwner() {
        // given
        UserDto expectedOwner = userService.create(ownerItem);

        UserDto expectedBooker = userService.create(booker);

        ItemDto expectedItem = itemService.add(expectedOwner.getId(), item);

        BookingCreateDto futureBooking = new BookingCreateDto();
        futureBooking.setItemId(expectedItem.getId());
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));

        bookingService.add(expectedBooker.getId(), futureBooking);

        BookingCreateDto pastBooking = new BookingCreateDto();
        pastBooking.setItemId(expectedItem.getId());
        pastBooking.setStart(LocalDateTime.now().minusDays(5));
        pastBooking.setEnd(LocalDateTime.now().minusDays(3));

        BookingDto pastBookingDto = bookingService.add(expectedBooker.getId(), pastBooking);

        BookingDto updateBooking = bookingService.update(expectedOwner.getId(), pastBookingDto.getId(), false);

        // when

        List<BookingDto> expectedList = bookingService.getBookingsForOwner(expectedOwner.getId(), BookingState.REJECTED, 0, 20);

        // then

        assertEquals(expectedList.size(), 1);
        assertEquals(expectedList.get(0).getItem().getId(), expectedItem.getId());
        assertEquals(expectedList.get(0).getBooker().getId(), expectedBooker.getId());
        assertEquals(expectedList.get(0).getStatus(), BookingStatus.REJECTED);
        assertEquals(expectedList.get(0).getStatus(), updateBooking.getStatus());

    }

    @Test
    public void testGetCurrentBookingForOwner() {
        // given
        UserDto expectedOwner = userService.create(ownerItem);

        UserDto expectedBooker = userService.create(booker);

        ItemDto expectedItem = itemService.add(expectedOwner.getId(), item);

        BookingCreateDto currentBooking = new BookingCreateDto();
        currentBooking.setItemId(expectedItem.getId());
        currentBooking.setStart(LocalDateTime.now());
        currentBooking.setEnd(LocalDateTime.now().plusDays(2));

        bookingService.add(expectedBooker.getId(), currentBooking);

        BookingCreateDto futureBooking = new BookingCreateDto();
        futureBooking.setItemId(expectedItem.getId());
        futureBooking.setStart(LocalDateTime.now().plusDays(10));
        futureBooking.setEnd(LocalDateTime.now().plusDays(12));

        bookingService.add(expectedBooker.getId(), futureBooking);

        // when

        List<BookingDto> expectedList = bookingService.getBookingsForOwner(expectedOwner.getId(), BookingState.CURRENT, 0, 20);

        // then

        assertEquals(expectedList.size(), 1);
        assertEquals(expectedList.get(0).getItem().getId(), expectedItem.getId());
        assertEquals(expectedList.get(0).getBooker().getId(), expectedBooker.getId());
    }

    @Test
    public void testGetBookingsForOwnerWithPageable() {
        // given
        UserDto expectedOwner = userService.create(ownerItem);
        UserDto expectedBooker = userService.create(booker);
        ItemDto expectedItem = itemService.add(expectedOwner.getId(), item);

        BookingCreateDto booking1 = new BookingCreateDto();
        booking1.setItemId(expectedItem.getId());
        booking1.setStart(LocalDateTime.now());
        booking1.setEnd(LocalDateTime.now().plusDays(2));
        bookingService.add(expectedBooker.getId(), booking1);

        BookingCreateDto booking2 = new BookingCreateDto();
        booking2.setItemId(expectedItem.getId());
        booking2.setStart(LocalDateTime.now().plusDays(10));
        booking2.setEnd(LocalDateTime.now().plusDays(12));
        bookingService.add(expectedBooker.getId(), booking2);

        // when

        List<BookingDto> resultList = bookingService.getBookingsForOwner(expectedOwner.getId(), BookingState.ALL, 0, 1);

        // then
        assertEquals(1, resultList.size());
        assertEquals(booking2.getStart(), resultList.get(0).getStart());
        assertEquals(booking2.getEnd(), resultList.get(0).getEnd());

    }

    @Test
    public void testGetAllBookingForBooker() {
        // given
        UserDto expectedOwner = userService.create(ownerItem);

        UserDto expectedBooker = userService.create(booker);

        ItemDto expectedItem = itemService.add(expectedOwner.getId(), item);

        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(expectedItem.getId());
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));

        bookingService.add(expectedBooker.getId(), bookingCreateDto);

        // when

        List<BookingDto> expectedList = bookingService.getBookingsForBooker(expectedBooker.getId(), BookingState.ALL, 0, 20);

        // then

        assertEquals(expectedList.size(), 1);
        assertEquals(expectedList.get(0).getItem().getId(), expectedItem.getId());
        assertEquals(expectedList.get(0).getBooker().getId(), expectedBooker.getId());
    }

    @Test
    public void testGetBookingsSortedByDateDescending() {
        // given
        UserDto expectedOwner = userService.create(ownerItem);

        UserDto expectedBooker = userService.create(booker);

        ItemDto expectedItem = itemService.add(expectedOwner.getId(), item);

        BookingCreateDto futureBooking = new BookingCreateDto();
        futureBooking.setItemId(expectedItem.getId());
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));

        bookingService.add(expectedBooker.getId(), futureBooking);

        BookingCreateDto pastBooking = new BookingCreateDto();
        pastBooking.setItemId(expectedItem.getId());
        pastBooking.setStart(LocalDateTime.now().minusDays(5));
        pastBooking.setEnd(LocalDateTime.now().minusDays(3));

        bookingService.add(expectedBooker.getId(), pastBooking);

        // when

        List<BookingDto> expectedList = bookingService.getBookingsForBooker(expectedBooker.getId(), BookingState.ALL, 0, 20);

        // then

        assertEquals(expectedList.size(), 2);
        assertEquals(expectedList.get(0).getStart(), futureBooking.getStart());
        assertEquals(expectedList.get(1).getStart(), pastBooking.getStart());
    }

    @Test
    public void testGetPastBookingForBooker() {
        // given
        UserDto expectedOwner = userService.create(ownerItem);

        UserDto expectedBooker = userService.create(booker);

        ItemDto expectedItem = itemService.add(expectedOwner.getId(), item);

        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(expectedItem.getId());
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));

        bookingService.add(expectedBooker.getId(), bookingCreateDto);

        BookingCreateDto pastBooking = new BookingCreateDto();
        pastBooking.setItemId(expectedItem.getId());
        pastBooking.setStart(LocalDateTime.now().minusDays(5));
        pastBooking.setEnd(LocalDateTime.now().minusDays(3));

        bookingService.add(expectedBooker.getId(), pastBooking);

        // when

        List<BookingDto> expectedList = bookingService.getBookingsForBooker(expectedBooker.getId(), BookingState.PAST, 0, 20);

        // then

        assertEquals(expectedList.size(), 1);
        assertEquals(expectedList.get(0).getItem().getId(), expectedItem.getId());
        assertEquals(expectedList.get(0).getBooker().getId(), expectedBooker.getId());
        assertEquals(expectedList.get(0).getStart(), pastBooking.getStart());
        assertEquals(expectedList.get(0).getEnd(), pastBooking.getEnd());
    }

    @Test
    public void testGetFutureBookingForBooker() {
        // given
        UserDto expectedOwner = userService.create(ownerItem);

        UserDto expectedBooker = userService.create(booker);

        ItemDto expectedItem = itemService.add(expectedOwner.getId(), item);

        BookingCreateDto futureBooking = new BookingCreateDto();
        futureBooking.setItemId(expectedItem.getId());
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));

        bookingService.add(expectedBooker.getId(), futureBooking);

        BookingCreateDto pastBooking = new BookingCreateDto();
        pastBooking.setItemId(expectedItem.getId());
        pastBooking.setStart(LocalDateTime.now().minusDays(5));
        pastBooking.setEnd(LocalDateTime.now().minusDays(3));

        bookingService.add(expectedBooker.getId(), pastBooking);

        // when

        List<BookingDto> expectedList = bookingService.getBookingsForBooker(expectedBooker.getId(), BookingState.FUTURE, 0, 20);

        // then

        assertEquals(expectedList.size(), 1);
        assertEquals(expectedList.get(0).getItem().getId(), expectedItem.getId());
        assertEquals(expectedList.get(0).getBooker().getId(), expectedBooker.getId());
        assertEquals(expectedList.get(0).getStart(), futureBooking.getStart());
        assertEquals(expectedList.get(0).getEnd(), futureBooking.getEnd());
    }

    @Test
    public void testGetWaitingBookingForBooker() {
        // given
        UserDto expectedOwner = userService.create(ownerItem);

        UserDto expectedBooker = userService.create(booker);

        ItemDto expectedItem = itemService.add(expectedOwner.getId(), item);

        BookingCreateDto futureBooking = new BookingCreateDto();
        futureBooking.setItemId(expectedItem.getId());
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto futureBookingDto = bookingService.add(expectedBooker.getId(), futureBooking);

        BookingCreateDto pastBooking = new BookingCreateDto();
        pastBooking.setItemId(expectedItem.getId());
        pastBooking.setStart(LocalDateTime.now().minusDays(5));
        pastBooking.setEnd(LocalDateTime.now().minusDays(3));

        BookingDto pastBookingDto = bookingService.add(expectedBooker.getId(), pastBooking);

        // when

        List<BookingDto> expectedList = bookingService.getBookingsForBooker(expectedBooker.getId(), BookingState.WAITING, 0, 20);

        // then

        assertEquals(expectedList.size(), 2);
        assertEquals(expectedList.get(0).getItem().getId(), expectedItem.getId());
        assertEquals(expectedList.get(0).getBooker().getId(), expectedBooker.getId());
        assertEquals(expectedList.get(1).getItem().getId(), expectedItem.getId());
        assertEquals(expectedList.get(1).getBooker().getId(), expectedBooker.getId());
        assertEquals(expectedList.get(0).getStatus(), BookingStatus.WAITING);
        assertEquals(expectedList.get(0).getStatus(), futureBookingDto.getStatus());
        assertEquals(expectedList.get(1).getStatus(), BookingStatus.WAITING);
        assertEquals(expectedList.get(1).getStatus(), pastBookingDto.getStatus());

    }

    @Test
    public void testGetRejectedBookingForBooker() {
        // given
        UserDto expectedOwner = userService.create(ownerItem);

        UserDto expectedBooker = userService.create(booker);

        ItemDto expectedItem = itemService.add(expectedOwner.getId(), item);

        BookingCreateDto futureBooking = new BookingCreateDto();
        futureBooking.setItemId(expectedItem.getId());
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));

        bookingService.add(expectedBooker.getId(), futureBooking);

        BookingCreateDto pastBooking = new BookingCreateDto();
        pastBooking.setItemId(expectedItem.getId());
        pastBooking.setStart(LocalDateTime.now().minusDays(5));
        pastBooking.setEnd(LocalDateTime.now().minusDays(3));

        BookingDto pastBookingDto = bookingService.add(expectedBooker.getId(), pastBooking);

        BookingDto updateBooking = bookingService.update(expectedOwner.getId(), pastBookingDto.getId(), false);

        // when

        List<BookingDto> expectedList = bookingService.getBookingsForBooker(expectedBooker.getId(), BookingState.REJECTED, 0, 20);

        // then

        assertEquals(expectedList.size(), 1);
        assertEquals(expectedList.get(0).getItem().getId(), expectedItem.getId());
        assertEquals(expectedList.get(0).getBooker().getId(), expectedBooker.getId());
        assertEquals(expectedList.get(0).getStatus(), BookingStatus.REJECTED);
        assertEquals(expectedList.get(0).getStatus(), updateBooking.getStatus());

    }

    @Test
    public void testGetCurrentBookingForBooker() {
        // given
        UserDto expectedOwner = userService.create(ownerItem);

        UserDto expectedBooker = userService.create(booker);

        ItemDto expectedItem = itemService.add(expectedOwner.getId(), item);

        BookingCreateDto currentBooking = new BookingCreateDto();
        currentBooking.setItemId(expectedItem.getId());
        currentBooking.setStart(LocalDateTime.now());
        currentBooking.setEnd(LocalDateTime.now().plusDays(2));

        bookingService.add(expectedBooker.getId(), currentBooking);

        BookingCreateDto futureBooking = new BookingCreateDto();
        futureBooking.setItemId(expectedItem.getId());
        futureBooking.setStart(LocalDateTime.now().plusDays(10));
        futureBooking.setEnd(LocalDateTime.now().plusDays(12));

        bookingService.add(expectedBooker.getId(), futureBooking);

        // when

        List<BookingDto> expectedList = bookingService.getBookingsForBooker(expectedBooker.getId(), BookingState.CURRENT, 0, 20);

        // then

        assertEquals(expectedList.size(), 1);
        assertEquals(expectedList.get(0).getItem().getId(), expectedItem.getId());
        assertEquals(expectedList.get(0).getBooker().getId(), expectedBooker.getId());
    }

    @Test
    public void testGetBookingsForBookerWithPageable() {
        // given
        UserDto expectedOwner = userService.create(ownerItem);
        UserDto expectedBooker = userService.create(booker);
        ItemDto expectedItem = itemService.add(expectedOwner.getId(), item);

        BookingCreateDto booking1 = new BookingCreateDto();
        booking1.setItemId(expectedItem.getId());
        booking1.setStart(LocalDateTime.now());
        booking1.setEnd(LocalDateTime.now().plusDays(2));
        bookingService.add(expectedBooker.getId(), booking1);

        BookingCreateDto booking2 = new BookingCreateDto();
        booking2.setItemId(expectedItem.getId());
        booking2.setStart(LocalDateTime.now().plusDays(10));
        booking2.setEnd(LocalDateTime.now().plusDays(12));
        bookingService.add(expectedBooker.getId(), booking2);

        // when

        List<BookingDto> resultList = bookingService.getBookingsForBooker(expectedBooker.getId(), BookingState.ALL, 0, 1);

        // then
        assertEquals(1, resultList.size());
        assertEquals(booking2.getStart(), resultList.get(0).getStart());
        assertEquals(booking2.getEnd(), resultList.get(0).getEnd());

    }


}
