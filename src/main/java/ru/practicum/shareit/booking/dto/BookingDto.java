package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingStatus;


import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {

    private Long id;

    @NotNull(message = "Время начала не должно быть пустым")
    private LocalDateTime start;

    @NotNull(message = "Время окончания не должно быть пустым")
    private LocalDateTime end;

    private BookingStatus status;

    private Long itemId;


}
