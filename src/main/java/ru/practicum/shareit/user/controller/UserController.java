package ru.practicum.shareit.user.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.valid.OnCreate;
import ru.practicum.shareit.validation.valid.OnUpdate;

import java.util.List;


@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto create(@Validated(OnCreate.class) @RequestBody UserDto userDto) {
        log.info("Создание пользователя: {}", userDto);
        UserDto createdUser = userService.create(userDto);
        log.info("Пользователь создан: {}", createdUser);
        return createdUser;
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Получение списка пользователей");
        List<UserDto> usersDto = userService.getAll();
        log.info("Получено {} пользователей", usersDto.size());
        return usersDto;
    }

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable long userId) {
        log.info("Получение пользователя с идентификатором: {}", userId);
        UserDto userDto = userService.getById(userId);
        log.info("Пользователь с идентификатором {} получен", userId);
        return userDto;
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable long userId, @Validated(OnUpdate.class) @RequestBody UserDto userDto) {
        log.info("Обновление имени пользователя с идентификатором: {}", userId);
        userDto.setId(userId);
        UserDto updatedUser = userService.update(userDto);
        log.info("Имя у пользователя с идентификатором: {} обновлен", userId);
        return updatedUser;
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        log.info("Удаление пользователя с идентификатором: {}", userId);
        userService.delete(userId);
        log.info("Пользователь с идентификатором {} удален", userId);
    }

}
