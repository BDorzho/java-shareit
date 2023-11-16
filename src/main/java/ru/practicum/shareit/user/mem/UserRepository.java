package ru.practicum.shareit.user.mem;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<UserDto> getAll();

    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto);

    void deleteById(Long userId);

    Optional<UserDto> findById(Long userId);

    Optional<UserDto> findByEmail(String email);

}
