package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAll();

    UserDto create(UserDto userDto);

    UserDto getById(long userId);

    UserDto update(long userId, UserDto userDto);

    void delete(long userId);

}
