package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.OnCreate;
import ru.practicum.shareit.validation.OnUpdate;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@Validated(OnCreate.class) @RequestBody UserDto userDto) {
        log.info("Создание пользователя: {}", userDto);
        ResponseEntity<Object> createdUser = userClient.createUser(userDto);
        log.info("Пользователь создан: {}", createdUser);
        return createdUser;
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Получение списка пользователей");
        ResponseEntity<Object> usersDto = userClient.getUsers();
        log.info("Получено {} пользователей", usersDto.getBody());
        return usersDto;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> get(@PathVariable long userId) {
        log.info("Получение пользователя с идентификатором: {}", userId);
        ResponseEntity<Object> userDto = userClient.getUser(userId);
        log.info("Пользователь с идентификатором {} получен", userId);
        return userDto;
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable long userId, @Validated(OnUpdate.class) @RequestBody UserDto userDto) {
        log.info("Обновление пользователя с идентификатором: {}", userId);
        ResponseEntity<Object> updatedUser = userClient.updateUser(userId, userDto);
        log.info("Пользователь с идентификатором: {} обновлен", userId);
        return updatedUser;
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        log.info("Удаление пользователя с идентификатором: {}", userId);
        userClient.deleteUser(userId);
        log.info("Пользователь с идентификатором {} удален", userId);
    }

}

