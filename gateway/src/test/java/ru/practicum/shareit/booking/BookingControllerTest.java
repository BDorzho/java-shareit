package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingClient bookingClient;

    @Autowired
    MockMvc mvc;

    @Test
    public void testGetBookingsByOwnerWithInvalidFrom() throws Exception {

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

        // then
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 123)
                        .param("from", "0")
                        .param("size", "-1")
                        .param("state", "ALL"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testAddBooking_failedByStartEqualNull() throws Exception {
        // given
        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(6));
        bookingCreateDto.setItemId(1L);

        // when

        when(bookingClient.add(1L, bookingCreateDto)).thenThrow(new IllegalArgumentException("Время старта не должно быть пустым"));

        // then
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isBadRequest());


        verify(bookingClient, times(0)).add(anyLong(), any(BookingCreateDto.class));
    }

    @Test
    public void testAddBooking_failedByEndEqualNull() throws Exception {
        // given
        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(6));
        bookingCreateDto.setItemId(1L);

        // when

        when(bookingClient.add(1L, bookingCreateDto)).thenThrow(new IllegalArgumentException("Время окончания не должно быть пустым"));

        // then
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isBadRequest());


        verify(bookingClient, times(0)).add(anyLong(), any(BookingCreateDto.class));
    }

    @Test
    public void testAddBooking_failedByEndInPast() throws Exception {
        // given
        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEnd(LocalDateTime.now().minusDays(1));
        bookingCreateDto.setItemId(1L);

        // when

        when(bookingClient.add(1L, bookingCreateDto)).thenThrow(new IllegalArgumentException("Дата окончания должна быть в будущем"));

        // then
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isBadRequest());


        verify(bookingClient, times(0)).add(anyLong(), any(BookingCreateDto.class));
    }

    @Test
    public void testAddBooking_failedByEndBeforeStart() throws Exception {
        // given
        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(5));
        bookingCreateDto.setEnd(LocalDateTime.now().minusDays(4));
        bookingCreateDto.setItemId(1L);

        // when

        when(bookingClient.add(1L, bookingCreateDto)).thenThrow(new IllegalArgumentException("Время окончания должно быть после старта"));

        // then
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isBadRequest());


        verify(bookingClient, times(0)).add(anyLong(), any(BookingCreateDto.class));
    }

    @Test
    public void testGetBookingsByUserWithWrongState() throws Exception {
        // given
        String wrongState = "UNSUPPORTED_STATUS";

        // when
        when(bookingClient.getBookings(anyLong(), any(BookingState.class), anyInt(), anyInt()))
                .thenThrow(new IllegalArgumentException("Unknown state: " + wrongState));

        // then
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", String.valueOf(1))
                        .param("state", wrongState))
                .andExpect(status().isBadRequest());
    }


}

