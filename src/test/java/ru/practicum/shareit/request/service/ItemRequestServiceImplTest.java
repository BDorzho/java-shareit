package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.exception.NotFoundException;

import javax.transaction.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImplTest {

    private final UserService userService;

    private final ItemService itemService;

    private final ItemRequestService itemRequestService;

    UserDto ownerItem;

    UserDto requesterUser;

    ItemDto item;

    ItemRequestDto itemRequestDto;


    @BeforeEach
    void setUp() {
        ownerItem = new UserDto();
        ownerItem.setName("owner item");
        ownerItem.setEmail("owner@email.ru");

        requesterUser = new UserDto();
        requesterUser.setName("test request");
        requesterUser.setEmail("test@email.ru");

        item = new ItemDto();
        item.setName("Item 1");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(ownerItem.getId());

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Description");
    }

    @Test
    public void testCreateRequest() {
        // given
        UserDto actualUser = userService.create(requesterUser);

        // when
        ItemRequestDto expectedItemRequest = itemRequestService.create(actualUser.getId(), itemRequestDto);

        // then
        assertNotNull(expectedItemRequest);
        assertNotNull(expectedItemRequest.getId());
        assertEquals(expectedItemRequest.getDescription(), itemRequestDto.getDescription());
        assertNotNull(expectedItemRequest.getCreated());
    }

    @Test
    public void testCreateRequestWithNotFoundUser() {
        // when
        Exception exception = assertThrows(NotFoundException.class, () -> itemRequestService.create(999L, itemRequestDto));

        // then
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    public void testGet() {
        // given
        UserDto owner = userService.create(ownerItem);

        UserDto actualUser = userService.create(requesterUser);

        ItemRequestDto expectedItemRequest = itemRequestService.create(actualUser.getId(), itemRequestDto);

        item.setRequestId(expectedItemRequest.getId());

        itemService.add(owner.getId(), item);

        // when
        List<ItemRequestInfoDto> result = itemRequestService.get(actualUser.getId());

        // then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

        ItemRequestInfoDto actualItemRequest = result.get(0);
        assertEquals(expectedItemRequest.getId(), actualItemRequest.getId());
        assertEquals(expectedItemRequest.getDescription(), actualItemRequest.getDescription());
        assertNotNull(actualItemRequest.getCreated());
        assertEquals(item.getName(), actualItemRequest.getItems().get(0).getName());
    }

    @Test
    public void testGetWithNotFoundUser() {
        // when
        Exception exception = assertThrows(NotFoundException.class, () -> itemRequestService.get(999L));

        // then
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    public void testGetAllItemRequest() {
        // given
        UserDto seeUser = userService.create(ownerItem);

        UserDto userCreateRequest = userService.create(requesterUser);

        ItemRequestDto expectedItemRequest = itemRequestService.create(userCreateRequest.getId(), itemRequestDto);

        // when
        List<ItemRequestInfoDto> expectedRequest = itemRequestService.getAll(seeUser.getId(), 0, 20);

        // then

        assertEquals(expectedRequest.size(), 1);
        assertEquals(expectedRequest.get(0).getId(), expectedItemRequest.getId());
        assertEquals(expectedRequest.get(0).getDescription(), expectedItemRequest.getDescription());

    }

    @Test
    public void testGetAllWhenEmpty() {
        // given
        UserDto seeUser = userService.create(ownerItem);

        // when
        List<ItemRequestInfoDto> expectedRequest = itemRequestService.getAll(seeUser.getId(), 0, 20);

        // then

        assertEquals(expectedRequest.size(), 0);

    }

    @Test
    public void testGetById() {
        // given
        UserDto createUser = new UserDto();
        createUser.setName("user see");
        createUser.setEmail("seeUser@email.ru");
        UserDto seeUser = userService.create(createUser);

        UserDto owner = userService.create(ownerItem);

        UserDto actualUser = userService.create(requesterUser);

        ItemRequestDto expectedItemRequest = itemRequestService.create(actualUser.getId(), itemRequestDto);

        item.setRequestId(expectedItemRequest.getId());

        itemService.add(owner.getId(), item);

        // when
        ItemRequestInfoDto result = itemRequestService.getById(seeUser.getId(), expectedItemRequest.getId());

        // then
        assertNotNull(result);
        assertEquals(expectedItemRequest.getId(), result.getId());
        assertEquals(expectedItemRequest.getDescription(), result.getDescription());
        assertEquals(expectedItemRequest.getCreated(), result.getCreated());
        assertEquals(1, result.getItems().size());
        assertEquals("Item 1", result.getItems().get(0).getName());

    }

    @Test
    public void testGetByIdUserNotFound() {
        // when
        Exception exception = assertThrows(NotFoundException.class, () -> itemRequestService.getById(9999L, 1L));

        // then
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    public void testGetByIdRequestNotFound() {
        // given
        UserDto actualUser = userService.create(requesterUser);

        // when
        Exception exception = assertThrows(NotFoundException.class, () -> itemRequestService.getById(actualUser.getId(), 9999L));

        // then
        assertEquals("Запрос не найден", exception.getMessage());
    }


}





