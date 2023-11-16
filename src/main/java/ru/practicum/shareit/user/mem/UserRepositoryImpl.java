package ru.practicum.shareit.user.mem;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, UserDto> users = new HashMap<>();
    private long nextId = 1;

    @Override
    public List<UserDto> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public UserDto create(UserDto userDto) {
        userDto.setId(nextId++);
        users.put(userDto.getId(), userDto);
        return userDto;
    }

    @Override
    public UserDto update(UserDto userDto) {
        users.put(userDto.getId(), userDto);
        return users.get(userDto.getId());
    }

    @Override
    public void deleteById(Long userId) {
        users.remove(userId);
    }

    @Override
    public Optional<UserDto> findById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public Optional<UserDto> findByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail() != null && user.getEmail().equals(email))
                .findFirst();
    }
}
