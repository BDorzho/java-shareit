package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

    @Mock
    private ItemClient itemClient;

    @InjectMocks
    private ItemController itemController;

    private MockMvc mvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.standaloneSetup(itemController).build();
    }


    @Test
    void testCreateItem_Fail_EmptyName() throws Exception {
        // given
        ItemDto itemDto = new ItemDto(1L, "", "Description", true, 100L, null);

        // when
        // then
        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).add(anyLong(), any(ItemDto.class));
    }


    @Test
    void testCreateItem_Fail_ShortName() throws Exception {
        // given
        ItemDto itemDto = new ItemDto(1L, "A", "Description", true, 100L, null);

        // when
        // then
        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).add(anyLong(), any(ItemDto.class));
    }

    @Test
    void testCreateItem_Fail_Description() throws Exception {
        // given
        ItemDto itemDto = new ItemDto(1L, "Test Item", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", true, 100L, null);
        // when
        // then
        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).add(anyLong(), any(ItemDto.class));
    }

    @Test
    void testCreateItem_Fail_EmptyDescription() throws Exception {
        // given
        ItemDto itemDto = new ItemDto(1L, "Test Item", "", true, 100L, null);
        // when
        // then
        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).add(anyLong(), any(ItemDto.class));
    }

    @Test
    void testCreateItem_Fail_NullAvailable() throws Exception {
        // given
        ItemDto itemDto = new ItemDto(1L, "Test Item", "Description", null, 100L, null);
        // when
        // then
        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).add(anyLong(), any(ItemDto.class));
    }
}


