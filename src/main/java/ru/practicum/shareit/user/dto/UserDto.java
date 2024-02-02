package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.valid.OnCreate;
import ru.practicum.shareit.validation.valid.OnUpdate;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private long id;

    @NotBlank(message = "Имя пользователя не может быть пустым", groups = {OnCreate.class})
    @Size(min = 3, max = 20, message = "Имя пользователя должно содержать от 3 до 20 символов.", groups = {OnCreate.class, OnUpdate.class})
    private String name;

    @NotBlank(message = "Email пользователя не может быть пустым", groups = OnCreate.class)
    @Email(message = "Некорректный формат email", groups = {OnCreate.class, OnUpdate.class})
    private String email;

}
