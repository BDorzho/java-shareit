package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.validation.exception.NotFoundException;
import ru.practicum.shareit.validation.exception.ValidationException;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    MockMvc mvc;

    @Test
    public void testAddBooking_thenReturnsCreated() throws Exception {
        // given
        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setStart(LocalDateTime.now().plusMinutes(10));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setItemId(1L);

        BookingDto expectedBookingDto = new BookingDto();

        // when

        when(bookingService.add(anyLong(), any())).thenReturn(expectedBookingDto);

        // then

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedBookingDto)));

        verify(bookingService, times(1)).add(anyLong(), any(BookingCreateDto.class));
    }

    @Test
    public void testAddBooking_failedByEndInPast() throws Exception {
        // given
        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEnd(LocalDateTime.now().minusDays(1));
        bookingCreateDto.setItemId(1L);

        // when

        when(bookingService.add(1L, bookingCreateDto)).thenThrow(new IllegalArgumentException("Дата окончания должна быть в будущем"));

        // then
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isBadRequest());


        verify(bookingService, times(0)).add(anyLong(), any(BookingCreateDto.class));
    }

    @Test
    public void testAddBooking_failedByEndBeforeStart() throws Exception {
        // given
        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(5));
        bookingCreateDto.setEnd(LocalDateTime.now().minusDays(4));
        bookingCreateDto.setItemId(1L);

        // when

        when(bookingService.add(1L, bookingCreateDto)).thenThrow(new IllegalArgumentException("Время окончания должно быть после старта"));

        // then
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isBadRequest());


        verify(bookingService, times(0)).add(anyLong(), any(BookingCreateDto.class));
    }

    @Test
    public void testAddBooking_failedByStartEqualNull() throws Exception {
        // given
        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(6));
        bookingCreateDto.setItemId(1L);

        // when

        when(bookingService.add(1L, bookingCreateDto)).thenThrow(new IllegalArgumentException("Время старта не должно быть пустым"));

        // then
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isBadRequest());


        verify(bookingService, times(0)).add(anyLong(), any(BookingCreateDto.class));
    }

    @Test
    public void testAddBooking_failedByEndEqualNull() throws Exception {
        // given
        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(6));
        bookingCreateDto.setItemId(1L);

        // when

        when(bookingService.add(1L, bookingCreateDto)).thenThrow(new IllegalArgumentException("Время окончания не должно быть пустым"));

        // then
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isBadRequest());


        verify(bookingService, times(0)).add(anyLong(), any(BookingCreateDto.class));
    }


    @Test
    public void whenUpdateBooking_thenReturnsUpdated() throws Exception {
        // given
        BookingDto expectedUpdatedBooking = new BookingDto();

        // when
        when(bookingService.update(anyLong(), anyLong(), anyBoolean())).thenReturn(expectedUpdatedBooking);

        // then
        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 123)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedUpdatedBooking)));
    }

    @Test
    public void testNonOwnerChangeStatusUpdate() throws Exception {
        // given
        Long nonOwnerUserId = 12345L;

        // when
        when(bookingService.update(anyLong(), anyLong(), anyBoolean())).thenThrow(new NotFoundException("Изменение статуса бронирования разрешено только владельцу"));

        // then
        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", nonOwnerUserId)
                        .param("approved", "true"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testOwnerRejectsBooking_thenReturnsUpdatedBookingWithRejectedStatus() throws Exception {
        // given
        Long ownerId = 123L;

        BookingDto expectedUpdatedBooking = new BookingDto();
        expectedUpdatedBooking.setStatus(BookingStatus.REJECTED);

        // when
        when(bookingService.update(anyLong(), anyLong(), anyBoolean())).thenReturn(expectedUpdatedBooking);

        // then
        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("approved", "false"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedUpdatedBooking)));
    }

    @Test
    public void testOwnerChangeStatusByAfterApprove_thenReturnBadRequest() throws Exception {
        // given
        BookingDto actualUpdatedBooking = new BookingDto();
        actualUpdatedBooking.setStatus(BookingStatus.WAITING);

        // when
        when(bookingService.update(anyLong(), anyLong(), anyBoolean())).thenThrow(new ValidationException("Невозможно подтвердить бронирование из-за неверного статуса"));

        // then
        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void testAuthorRequestsBookingDetails_thenReturnsBookingDetails() throws Exception {
        // given
        long authorId = 123;
        Long bookingId = 1L;
        BookingDto expectedBookingDto = new BookingDto();

        // when
        when(bookingService.get(authorId, bookingId)).thenReturn(expectedBookingDto);

        // then
        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", authorId))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedBookingDto)));
    }

    @Test
    public void thenOwnerRequestsBookingDetails_thenReturnsBookingDetails() throws Exception {
        // given
        long ownerId = 456;
        Long bookingId = 1L;

        BookingDto expectedBookingDetails = new BookingDto();

        // when
        when(bookingService.get(ownerId, bookingId)).thenReturn(expectedBookingDetails);

        // then
        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedBookingDetails)));
    }

    @Test
    public void testNonRelatedUserRequestsBookingDetails_thenThrowsNotFoundException() throws Exception {
        // given
        long unrelatedUserId = 789;
        Long bookingId = 1L;

        // when
        when(bookingService.get(unrelatedUserId, bookingId)).thenThrow(new NotFoundException("Пользователь не автор бронирования и не владелец вещи"));

        // then
        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", unrelatedUserId))
                .andExpect(status().isNotFound());

    }

    @Test
    public void testGetBookingsByUser_thenReturnsListOfBookings() throws Exception {
        // given
        List<BookingDto> expectedBookings = new ArrayList<>();

        // when
        when(bookingService.getBookingsForBooker(anyLong(), any(), anyInt(), anyInt())).thenReturn(expectedBookings);

        // then
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 123)
                        .param("from", "0")
                        .param("size", "20")
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedBookings)));
    }


    @Test
    public void testGetBookingsByOwner_thenReturnsListOfBookings() throws Exception {
        // given
        List<BookingDto> expectedBookings = new ArrayList<>();

        // when
        when(bookingService.getBookingsForOwner(anyLong(), any(), anyInt(), anyInt())).thenReturn(expectedBookings);

        // then
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 123)
                        .param("from", "0")
                        .param("size", "20")
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedBookings)));
    }

    @Test
    public void testGetBookingsByUserWithWrongState() throws Exception {
        // given
        String wrongState = "UNSUPPORTED_STATUS";

        // when
        when(bookingService.getBookingsForBooker(anyLong(), any(BookingState.class), anyInt(), anyInt()))
                .thenThrow(new IllegalArgumentException("Unknown state: " + wrongState));

        // then
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", String.valueOf(1))
                        .param("state", wrongState))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testGetBookingsByOwnerWithValidFromAndSize() throws Exception {
        // given
        List<BookingDto> expectedBookings = new ArrayList<>();

        // when
        when(bookingService.getBookingsForOwner(anyLong(), any(), anyInt(), anyInt())).thenReturn(expectedBookings);

        // then
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 123)
                        .param("from", "0")
                        .param("size", "20")
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedBookings)));

        verify(bookingService).getBookingsForOwner(eq(123L), eq(BookingState.ALL), eq(0), eq(20));
    }

    @Test
    public void testGetBookingsByOwnerWithInvalidFrom() throws Exception {
        // given
        List<BookingDto> expectedBookings = new ArrayList<>();

        // then
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 123)
                        .param("from", "-1")
                        .param("size", "20")
                        .param("state", "ALL"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testGetBookingsByOwnerWithInvalidSize() throws Exception {
        // given
        List<BookingDto> expectedBookings = new ArrayList<>();

        // then
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 123)
                        .param("from", "0")
                        .param("size", "-1")
                        .param("state", "ALL"))
                .andExpect(status().isInternalServerError());
    }


}

