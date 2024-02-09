package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingCreateDto {

    private Long id;

    @NotNull(message = "Время старта не должно быть пустым")
    @Future(message = "Дата старта должна быть в будущем")
    private LocalDateTime start;

    @NotNull(message = "Время окончания не должно быть пустым")
    @Future(message = "Дата окончания должна быть в будущем")
    private LocalDateTime end;

    @AssertTrue(message = "Время окончания должно быть после старта")
    private boolean isEndAfterStart() {
        return start == null || end == null || end.isAfter(start);
    }

    @NotNull
    private Long itemId;


}
