package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @MockBean
    ItemRequestService itemRequestService;


    @Test
    public void testCreate() throws Exception {
        // given
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Description");
        itemRequestDto.setCreated(LocalDateTime.now());


        // when
        when(itemRequestService.create(1, itemRequestDto)).thenReturn(itemRequestDto);

        // then
        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemRequestDto)));

        verify(itemRequestService, times(1)).create(anyLong(), any(ItemRequestDto.class));

    }

    @Test
    public void testGet() throws Exception {
        // given
        List<ItemRequestInfoDto> itemRequestDtoList = new ArrayList<>();

        // when
        when(itemRequestService.get(1)).thenReturn(itemRequestDtoList);

        // then
        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(itemRequestService, times(1)).get(anyLong());
    }

    @Test
    public void testGetAll() throws Exception {
        // given
        List<ItemRequestInfoDto> itemRequestDtoList = new ArrayList<>();

        // when
        when(itemRequestService.getAll(1, 0, 20)).thenReturn(itemRequestDtoList);

        // then
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(20)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void testGetAllWithInvalidFrom() throws Exception {
        // given
        List<ItemRequestInfoDto> itemRequestDtoList = new ArrayList<>();

        // when
        when(itemRequestService.getAll(1, 0, 20)).thenReturn(itemRequestDtoList);

        // then
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", String.valueOf(-1))
                        .param("size", String.valueOf(20)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testGetAllWithInvalidSize() throws Exception {
        // given

        List<ItemRequestInfoDto> itemRequestDtoList = new ArrayList<>();

        // when
        when(itemRequestService.getAll(1, 0, 20)).thenReturn(itemRequestDtoList);

        // then
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(-1)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testGetById() throws Exception {
        // given
        ItemRequestInfoDto itemRequestInfoDto = new ItemRequestInfoDto();
        itemRequestInfoDto.setId(1L);
        itemRequestInfoDto.setDescription("Description");
        itemRequestInfoDto.setCreated(LocalDateTime.now());
        itemRequestInfoDto.setItems(new ArrayList<>());

        // when
        when(itemRequestService.getById(1, 1)).thenReturn(itemRequestInfoDto);

        // then
        mvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemRequestInfoDto)));

        verify(itemRequestService, times(1)).getById(anyLong(), anyLong());
    }
}
