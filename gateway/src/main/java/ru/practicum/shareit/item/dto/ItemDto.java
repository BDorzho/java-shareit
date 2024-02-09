package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.OnCreate;
import ru.practicum.shareit.validation.OnUpdate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {

    private Long id;

    @NotBlank(message = "Название вещи не может быть пустым", groups = OnCreate.class)
    @Size(min = 3, max = 20, message = "Имя должно содержать от 3 до 20 символов.", groups = {OnCreate.class, OnUpdate.class})
    private String name;

    @NotBlank(message = "Описание вещи не может быть пустым", groups = OnCreate.class)
    @Size(max = 200, message = "Описание должно содержать не более 200 символов.", groups = {OnCreate.class, OnUpdate.class})
    private String description;

    @NotNull(message = "Статус доступности вещи не может быть пустым", groups = OnCreate.class)
    private Boolean available;

    private long owner;

    private Long requestId;

}
