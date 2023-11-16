package ru.practicum.shareit.user.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;


import java.util.List;


@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
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
    public UserDto get(@PathVariable Long userId) {
        log.info("Получение пользователя с идентификатором: {}", userId);
        UserDto userDto = userService.getById(userId);
        log.info("Пользователь с идентификатором {} получен", userId);
        return userDto;
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.info("Обновление пользователя с идентификатором: {}", userId);
        UserDto updatedUser = userService.update(userId, userDto);
        log.info("Пользователь с идентификатором: {} обновлен", userId);
        return updatedUser;
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        log.info("Удаление пользователя с идентификатором: {}", userId);
        userService.delete(userId);
        log.info("Пользователь с идентификатором {} удален", userId);
    }

}
