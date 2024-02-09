package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.exception.NotFoundException;
import ru.practicum.shareit.validation.exception.ValidationException;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplTest {

    private final ItemService itemService;

    private final UserService userService;

    private final BookingService bookingService;

    private final ItemMapper itemMapper;

    private final UserMapper userMapper;

    private final EntityManager em;

    User owner;
    Item item;

    UserDto createUser;

    Pageable pageable = PageRequest.of(0, 20);


    @BeforeEach
    void setUp() {

        owner = new User();
        owner.setName("Test User");
        owner.setEmail("test@email.com");

        createUser = new UserDto();
        createUser.setName("Test User");
        createUser.setEmail("test@email.com");

        item = new Item();
        item.setName("Item 1");
        item.setDescription("Description Item 1");
        item.setAvailable(true);
        item.setOwner(owner);
    }

    @Test
    public void testCreateItem() {
        // given
        UserDto ownerDto = userService.create(userMapper.toDto(owner));

        // when
        itemService.add(ownerDto.getId(), itemMapper.toDto(item));

        // then
        TypedQuery<Item> query =
                em.createQuery("Select i from Item i", Item.class);
        Item saveItem = query.getSingleResult();

        assertNotNull(saveItem.getId());
        assertEquals(saveItem.getName(), item.getName());
        assertEquals(saveItem.getDescription(), item.getDescription());
        assertEquals(saveItem.getAvailable(), item.getAvailable());
    }

    @Test
    public void testCreateItemWithNotFoundUser() {
        //when
        Exception exception = assertThrows(NotFoundException.class, () -> itemService.add(1, itemMapper.toDto(item)));

        // then
        assertTrue(exception.getMessage().contains("Пользователь не найден"));
    }

    @Test
    public void testUpdateItem() {
        // given
        ItemDto updateItem = new ItemDto();
        updateItem.setId(1L);
        updateItem.setName("Updated Item");
        updateItem.setDescription("Updated Description");
        updateItem.setAvailable(false);

        owner.setId(1L);
        item.setId(1L);

        UserRepository userRepository = Mockito.mock(UserRepository.class);
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ItemService itemService2 = new ItemServiceImpl(itemRepository, userRepository, null, null, itemMapper);

        // when
        ItemDto updatedItemDto = itemService2.update(1L, updateItem);

        // then
        assertEquals(updateItem.getId(), updatedItemDto.getId());
        assertEquals(updateItem.getName(), updatedItemDto.getName());
        assertEquals(updateItem.getDescription(), updatedItemDto.getDescription());
        assertEquals(updateItem.getAvailable(), updatedItemDto.getAvailable());

        verify(itemRepository, times(1)).save(item);
    }

    @Test
    public void testUpdateItemWithOtherUser() {
        // given
        User owner = new User();
        owner.setId(1);

        User otherUser = new User();
        otherUser.setId(2);

        Item item = new Item();
        item.setId(1L);
        item.setName("Item");
        item.setOwner(owner);

        ItemDto updateItem = new ItemDto();
        updateItem.setId(1L);
        updateItem.setName("Updated Item");
        updateItem.setDescription("Updated Description");
        updateItem.setAvailable(false);

        UserRepository userRepository = Mockito.mock(UserRepository.class);
        when(userRepository.findById(2L)).thenReturn(Optional.of(otherUser));

        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));


        ItemService actualItemService = new ItemServiceImpl(itemRepository, userRepository, null, null, itemMapper);

        // when
        Exception exception = assertThrows(NotFoundException.class, () -> actualItemService.update(2L, updateItem));

        // then
        assertEquals("Вещь может редактировать только ёё владелец", exception.getMessage());
    }

    @Test
    public void testGetByIdWhenItemExists() {
        // given
        User actualUser = new User();
        actualUser.setId(1);

        Item actualItem = new Item();
        actualItem.setId(1L);
        actualItem.setName("Item");
        actualItem.setDescription("Description");
        actualItem.setAvailable(true);
        actualItem.setOwner(actualUser);


        List<Comment> comments = new ArrayList<>();
        List<Booking> bookings = new ArrayList<>();

        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(actualItem));

        UserRepository userRepository = Mockito.mock(UserRepository.class);
        when(userRepository.findById(1L)).thenReturn(Optional.of(actualUser));

        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        when(commentRepository.findByItemId(1L)).thenReturn(comments);

        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        when(bookingRepository.findByItemId(1L)).thenReturn(bookings);

        ItemService actualItemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository, itemMapper);

        // when
        ItemInfoDto expectedItem = actualItemService.getById(1L, 1);

        // then

        assertEquals(expectedItem.getId(), actualItem.getId());
        assertEquals(expectedItem.getName(), actualItem.getName());
        assertEquals(expectedItem.getDescription(), actualItem.getDescription());
        assertEquals(expectedItem.getAvailable(), actualItem.getAvailable());


    }

    @Test
    public void testGetByIdWhenItemNotFound() {
        // given
        userService.create(userMapper.toDto(owner));

        // when
        Exception exception = assertThrows(NotFoundException.class, () -> itemService.getById(1L, 1L));

        // then
        assertEquals("Вещь не найдена", exception.getMessage());
    }

    @Test
    public void testSearchItems() {
        // given
        UserDto actualUser = userService.create(userMapper.toDto(owner));
        itemService.add(actualUser.getId(), itemMapper.toDto(item));

        // when
        itemService.search("item", pageable);

        // then
        TypedQuery<Item> query =
                em.createQuery("SELECT i FROM Item i " +
                        "WHERE (UPPER(i.name) LIKE UPPER(CONCAT('%', :searchText, '%')) " +
                        "OR UPPER(i.description) LIKE UPPER(CONCAT('%', :searchText, '%'))) " +
                        "AND i.available = true", Item.class);
        query.setParameter("searchText", "item");
        Item findItem = query.getSingleResult();


        assertNotNull(findItem.getId());
        assertEquals(findItem.getName(), item.getName());
        assertEquals(findItem.getDescription(), item.getDescription());
        assertEquals(findItem.getAvailable(), item.getAvailable());

    }

    @Test
    public void testSearchItemsWhenNotAvailable() {
        // given
        UserDto actualUser = userService.create(userMapper.toDto(owner));
        item.setAvailable(false);
        itemService.add(actualUser.getId(), itemMapper.toDto(item));

        // when
        List<ItemDto> expectedItem = itemService.search("item", pageable);

        // then
        assertThat(expectedItem).isEmpty();
    }

    @Test
    public void testSearchEmpty() {
        // given
        UserDto actualUser = userService.create(userMapper.toDto(owner));
        item.setAvailable(false);
        itemService.add(actualUser.getId(), itemMapper.toDto(item));

        // when
        List<ItemDto> expectedItem = itemService.search("", pageable);

        // then
        assertThat(expectedItem).isEmpty();
    }


    @Test
    public void testGetItems() throws Exception {
        // given
        UserDto ownerDto = userService.create(userMapper.toDto(owner));

        ItemDto itemDto = itemService.add(ownerDto.getId(), itemMapper.toDto(item));

        // when
        List<ItemInfoDto> list = itemService.getItems(ownerDto.getId(), pageable);

        // then
        assertEquals(list.size(), 1);
        assertEquals(list.get(0).getName(), itemDto.getName());
        assertEquals(list.get(0).getDescription(), itemDto.getDescription());
        assertEquals(list.get(0).getAvailable(), itemDto.getAvailable());
    }

    @Test
    public void testGetAllItemsDetailsWithBookingAndComment() {
        // given
        UserDto expectedOwner = userService.create(createUser);

        UserDto booker = new UserDto();
        booker.setName("test booker");
        booker.setEmail("test@email.ru");

        UserDto expectedBooker = userService.create(booker);

        ItemDto createItem = new ItemDto();
        createItem.setName("Item 1");
        createItem.setDescription("Description Item 1");
        createItem.setAvailable(true);
        createItem.setOwner(expectedOwner.getId());

        ItemDto expectedItem = itemService.add(expectedOwner.getId(), createItem);

        BookingCreateDto futureBooking = new BookingCreateDto();
        futureBooking.setItemId(expectedItem.getId());
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto nextBooking = bookingService.add(expectedBooker.getId(), futureBooking);

        BookingCreateDto pastBooking = new BookingCreateDto();
        pastBooking.setItemId(expectedItem.getId());
        pastBooking.setStart(LocalDateTime.now().minusDays(5));
        pastBooking.setEnd(LocalDateTime.now().minusDays(3));

        BookingDto lastBooking = bookingService.add(expectedBooker.getId(), pastBooking);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("comment");

        CommentDto expectedComment = itemService.addCommentToItem(expectedItem.getId(), expectedBooker.getId(), commentDto);

        // when
        List<ItemInfoDto> expectedItemDetails = itemService.getItems(expectedOwner.getId(), pageable);

        // then
        assertEquals(expectedItemDetails.size(), 1);
        ItemInfoDto actualInfoDto = expectedItemDetails.get(0);
        assertEquals(actualInfoDto.getName(), expectedItem.getName());
        assertEquals(actualInfoDto.getLastBooking().getId(), lastBooking.getId());
        assertEquals(actualInfoDto.getNextBooking().getId(), nextBooking.getId());
        assertEquals(actualInfoDto.getComments().get(0).getId(), expectedComment.getId());
    }


    @Test
    public void testDelete() {
        // given
        User user = new User();
        user.setId(1L);
        Item item = new Item();
        item.setId(2L);
        item.setOwner(user);

        UserRepository userRepository = Mockito.mock(UserRepository.class);

        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        Mockito.doNothing().when(itemRepository).deleteByIdAndOwnerId(2L, 1L);

        ItemService actualItemService = new ItemServiceImpl(itemRepository, userRepository, null, null, null);


        // when
        actualItemService.delete(1L, 2L);

        // then
        verify(itemRepository).deleteByIdAndOwnerId(2L, 1L);
    }

    @Test
    public void testAddCommentToItem() {
        // given
        UserDto expectedOwner = userService.create(createUser);

        UserDto booker = new UserDto();
        booker.setName("test booker");
        booker.setEmail("test@email.ru");

        UserDto expectedBooker = userService.create(booker);

        ItemDto createItem = new ItemDto();
        createItem.setName("Item 1");
        createItem.setDescription("Description Item 1");
        createItem.setAvailable(true);
        createItem.setOwner(expectedOwner.getId());

        ItemDto expectedItem = itemService.add(expectedOwner.getId(), createItem);

        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(expectedItem.getId());
        bookingCreateDto.setStart(LocalDateTime.now().minusDays(10));
        bookingCreateDto.setEnd(LocalDateTime.now().minusDays(20));

        BookingDto bookingDto = bookingService.add(expectedBooker.getId(), bookingCreateDto);

        bookingService.update(expectedOwner.getId(), bookingDto.getId(), true);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("comment");

        // when
        CommentDto expectedComment = itemService.addCommentToItem(expectedItem.getId(), expectedBooker.getId(), commentDto);

        // then
        assertNotNull(expectedComment);
        assertEquals(expectedComment.getAuthorName(), expectedBooker.getName());
        assertEquals(expectedComment.getText(), commentDto.getText());
    }

    @Test
    public void testAddCommentToItem_UserNotFound() {
        // given
        CommentDto commentDto = new CommentDto();

        // when
        Exception exception = assertThrows(NotFoundException.class, () -> itemService.addCommentToItem(1L, 1, commentDto));

        // then
        assertEquals("Пользователь не найден", exception.getMessage());
    }


    @Test
    public void testAddCommentToItem_ItemNotFound() {
        // given
        UserDto expectedUser = userService.create(createUser);
        CommentDto commentDto = new CommentDto();

        // when
        Exception exception = assertThrows(NotFoundException.class, () -> itemService.addCommentToItem(1L, expectedUser.getId(), commentDto));

        // then
        assertEquals("Вещь не найдена", exception.getMessage());
    }

    @Test
    public void testAddCommentToItem_UserHasNotBookedItem() {
        // given
        UserDto otherUser = new UserDto();
        otherUser.setName("test other");
        otherUser.setEmail("other@email.ru");

        UserDto wrongUser = userService.create(otherUser);

        UserDto expectedOwner = userService.create(createUser);

        UserDto booker = new UserDto();
        booker.setName("test booker");
        booker.setEmail("test@email.ru");

        UserDto expectedBooker = userService.create(booker);

        ItemDto createItem = new ItemDto();
        createItem.setName("Item 1");
        createItem.setDescription("Description Item 1");
        createItem.setAvailable(true);
        createItem.setOwner(expectedOwner.getId());

        ItemDto expectedItem = itemService.add(expectedOwner.getId(), createItem);

        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(expectedItem.getId());
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(10));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(20));

        BookingDto bookingDto = bookingService.add(expectedBooker.getId(), bookingCreateDto);

        bookingService.update(expectedOwner.getId(), bookingDto.getId(), true);

        CommentDto commentDto = new CommentDto();

        // when
        Exception exception = assertThrows(ValidationException.class, () -> itemService.addCommentToItem(expectedItem.getId(), wrongUser.getId(), commentDto));

        // then
        assertEquals("Пользователь не имеет права оставлять комментарии для этой вещи", exception.getMessage());
    }

    @Test
    public void testAddCommentToItem_failedByFutureBooking() {
        // given
        UserDto expectedOwner = userService.create(createUser);

        UserDto booker = new UserDto();
        booker.setName("test booker");
        booker.setEmail("test@email.ru");

        UserDto expectedBooker = userService.create(booker);

        ItemDto createItem = new ItemDto();
        createItem.setName("Item 1");
        createItem.setDescription("Description Item 1");
        createItem.setAvailable(true);
        createItem.setOwner(expectedOwner.getId());

        ItemDto expectedItem = itemService.add(expectedOwner.getId(), createItem);

        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(expectedItem.getId());
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(10));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(20));

        BookingDto bookingDto = bookingService.add(expectedBooker.getId(), bookingCreateDto);

        bookingService.update(expectedOwner.getId(), bookingDto.getId(), true);

        CommentDto commentDto = new CommentDto();

        // when
        Exception exception = assertThrows(ValidationException.class, () -> {
            itemService.addCommentToItem(expectedItem.getId(), expectedBooker.getId(), commentDto);
        });

        // then
        assertEquals("Пользователь не имеет права оставлять комментарии для этой вещи", exception.getMessage());
    }


}
