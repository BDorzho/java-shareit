package ru.practicum.shareit.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import ru.practicum.shareit.validation.exception.ValidationException;


@Service
@Slf4j
public class ValidationService {
    public void validateUser(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isEmpty() || !userDto.getEmail().contains("@")) {
            throwValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (userDto.getName() == null || userDto.getName().isEmpty()) {
            throwValidationException("Имя не может быть пустым");
        }
    }

    public void validateItem(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isEmpty() || itemDto.getAvailable() == null || itemDto.getDescription() == null) {
            System.out.println(itemDto);
            throwValidationException("Не все поля заполнены");
        }
    }

    private void throwValidationException(String message) {
        log.error("Ошибка валидации");
        throw new ValidationException(message);
    }
}
