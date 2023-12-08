package ru.practicum.shareit.user.mem;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.exception.ConflictException;
import ru.practicum.shareit.validation.exception.NotFoundException;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, Long> userEmails = new HashMap<>();

    private final AtomicLong idGenerator = new AtomicLong(0);

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        if (userEmails.containsKey(user.getEmail())) {
            throw new ConflictException("Пользователь с таким e-mail уже существует");
        }
        user.setId(idGenerator.incrementAndGet());
        users.put(user.getId(), user);
        userEmails.put(user.getEmail(), user.getId());
        return user;
    }

    @Override
    public void update(User user) {
        User updateUser = findById(user.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        String newEmail = user.getEmail();
        if (newEmail != null && !newEmail.equals(updateUser.getEmail())) {
            if (userEmails.containsKey(newEmail)) {
                throw new ConflictException("Пользователь с таким e-mail уже существует");
            }
            userEmails.remove(updateUser.getEmail());
            userEmails.put(user.getEmail(), user.getId());
            updateUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            updateUser.setName(user.getName());
        }
        users.put(updateUser.getId(), updateUser);
    }

    @Override
    public void deleteById(Long userId) {
        userEmails.remove(users.get(userId).getEmail());
        users.remove(userId);
    }

    @Override
    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

}
