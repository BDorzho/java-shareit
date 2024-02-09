package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    Pageable pageable = PageRequest.of(0, 20);

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
    }

    @Test
    public void testGetItems() throws Exception {

        long userId = 1;

        List<ItemInfoDto> expectedItems = Arrays.asList(
                new ItemInfoDto(1L, "Item 1", "Description 1", true, null, null, null),
                new ItemInfoDto(2L, "Item 2", "Description 2", true, null, null, null)
        );

        when(itemService.getItems(userId, pageable)).thenReturn(expectedItems);


        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(20)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Item 1")))
                .andExpect(jsonPath("$[0].description", is("Description 1")))
                .andExpect(jsonPath("$[0].available", is(true)))
                .andExpect(jsonPath("$[0].lastBooking", is(nullValue())))
                .andExpect(jsonPath("$[0].nextBooking", is(nullValue())))
                .andExpect(jsonPath("$[0].comments", is(nullValue())))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Item 2")))
                .andExpect(jsonPath("$[1].description", is("Description 2")))
                .andExpect(jsonPath("$[1].available", is(true)))
                .andExpect(jsonPath("$[1].lastBooking", is(nullValue())))
                .andExpect(jsonPath("$[1].nextBooking", is(nullValue())))
                .andExpect(jsonPath("$[1].comments", is(nullValue())));


        verify(itemService, times(1)).getItems(userId, pageable);
        verifyNoMoreInteractions(itemService);
    }

    @Test
    public void testGetItemById() throws Exception {

        long userId = 1;
        Long itemId = 1L;
        ItemInfoDto itemInfoDto = new ItemInfoDto(itemId, "Item", "Description", true, null, null, null);

        when(itemService.getById(itemId, userId)).thenReturn(itemInfoDto);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(itemInfoDto.getId()), Long.class))
                .andExpect(jsonPath("name", is(itemInfoDto.getName())))
                .andExpect(jsonPath("description", is(itemInfoDto.getDescription())))
                .andExpect(jsonPath("available", is(itemInfoDto.getAvailable())))
                .andExpect(jsonPath("lastBooking", is(itemInfoDto.getLastBooking())))
                .andExpect(jsonPath("nextBooking", is(itemInfoDto.getNextBooking())))
                .andExpect(jsonPath("comments", is(itemInfoDto.getComments())));

        verify(itemService, times(1)).getById(itemId, userId);
    }

    @Test
    public void testAddItem() throws Exception {

        long userId = 1;
        ItemDto itemDto = new ItemDto(1L, "New Item", "New Description", true, 1L, 1L);

        when(itemService.add(userId, itemDto)).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("name", is(itemDto.getName())))
                .andExpect(jsonPath("description", is(itemDto.getDescription())))
                .andExpect(jsonPath("available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("owner", is(itemDto.getOwner()), Long.class))
                .andExpect(jsonPath("requestId", is(itemDto.getRequestId()), Long.class));

        verify(itemService, times(1)).add(userId, itemDto);
    }

    @Test
    public void testDeleteItem() throws Exception {

        long userId = 1;
        Long itemId = 1L;

        mockMvc.perform(delete("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isOk());

        verify(itemService, times(1)).delete(userId, itemId);
    }

    @Test
    public void testUpdateItem() throws Exception {

        long userId = 1;
        Long itemId = 1L;
        ItemDto itemDto = new ItemDto(itemId, "Update Item", "Update Description", true, 1L, 1L);

        when(itemService.update(userId, itemDto)).thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("name", is(itemDto.getName())))
                .andExpect(jsonPath("description", is(itemDto.getDescription())))
                .andExpect(jsonPath("available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("owner", is(itemDto.getOwner()), Long.class))
                .andExpect(jsonPath("requestId", is(itemDto.getRequestId()), Long.class));

        verify(itemService, times(1)).update(userId, itemDto);
    }

    @Test
    public void testSearchItems() throws Exception {

        String searchText = "search_text";

        List<ItemDto> searchResult = Arrays.asList(
                new ItemDto(1L, "Item 1", "Description 1", true, 1, 1L),
                new ItemDto(2L, "Item 2", "Description 2", true, 2, 2L)
        );

        when(itemService.search(searchText, pageable)).thenReturn(searchResult);


        mockMvc.perform(get("/items/search")
                        .param("text", searchText)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(20)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Item 1")))
                .andExpect(jsonPath("$[0].description", is("Description 1")))
                .andExpect(jsonPath("$[0].available", is(true)))
                .andExpect(jsonPath("$[0].owner", is(1)))
                .andExpect(jsonPath("$[0].requestId", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Item 2")))
                .andExpect(jsonPath("$[1].description", is("Description 2")))
                .andExpect(jsonPath("$[1].available", is(true)))
                .andExpect(jsonPath("$[1].owner", is(2)))
                .andExpect(jsonPath("$[1].requestId", is(2)));

        verify(itemService, times(1)).search(searchText, pageable);
    }

    @Test
    public void testAddCommentToItem() throws Exception {

        long userId = 1;
        Long itemId = 1L;
        CommentDto commentDto = new CommentDto(1L, "Comment", "User", null);

        when(itemService.addCommentToItem(itemId, userId, commentDto)).thenReturn(commentDto);


        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("text", is(commentDto.getText())))
                .andExpect(jsonPath("authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("created", is(commentDto.getCreated())));

        verify(itemService, times(1)).addCommentToItem(itemId, userId, commentDto);
    }
}
