package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mem.UserRepository;
import ru.practicum.shareit.validation.ValidationService;
import ru.practicum.shareit.validation.exception.ConflictException;
import ru.practicum.shareit.validation.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ValidationService validationService;

    @Override
    public List<UserDto> getAll() {
        return userRepository.getAll();
    }

    @Override
    public UserDto create(UserDto userDto) {
        validationService.validateUser(userDto);
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new ConflictException("Пользователь с таким e-mail уже существует");
        }
        return userRepository.create(userDto);
    }

    @Override
    public UserDto getById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {

        UserDto updateUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (userDto.getName() != null) {
            updateUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().equals(updateUser.getEmail())) {
            if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
                throw new ConflictException("Пользователь с таким e-mail уже существует");
            }
            updateUser.setEmail(userDto.getEmail());
        }
        return userRepository.update(updateUser);
    }

    @Override
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }
}
