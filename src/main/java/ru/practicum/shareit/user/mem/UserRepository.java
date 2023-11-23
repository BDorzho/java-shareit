package ru.practicum.shareit.user.mem;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> getAll();

    User create(User user);

    User update(User user);

    void deleteById(Long userId);

    Optional<User> findById(Long userId);

}
