package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper mapper;

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAll() {
        return mapper.toListDto(userRepository.findAll());
    }

    @Transactional
    @Override
    public UserDto create(UserDto userDto) {
        User user = mapper.toModel(userDto);
        return mapper.toDto(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getById(long userId) {
        return mapper.toDto(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден")));
    }

    @Transactional
    @Override
    public UserDto update(UserDto userDto) {
        User updateUser = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (userDto.getName() != null) {
            updateUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            updateUser.setEmail(userDto.getEmail());
        }
        userRepository.save(updateUser);
        return mapper.toDto(updateUser);
    }

    @Override
    public void delete(long userId) {
        userRepository.deleteById(userId);
    }
}
