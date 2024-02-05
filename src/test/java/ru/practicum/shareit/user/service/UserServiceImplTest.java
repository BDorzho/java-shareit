package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    UserDto createUser;

    User user;

    @BeforeEach
    void setUp() {
        createUser = new UserDto();
        createUser.setId(1);
        createUser.setName("test name");
        createUser.setEmail("test@mail.ru");

        user = new User();
        user.setId(1);
        user.setName("test name");
        user.setEmail("test@mail.ru");

    }

    @Test
    public void testCreate() {
        // given
        when(userMapper.toModel(createUser)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        // when
        userService.create(createUser);

        // then
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testGetAll() {
        // given
        List<User> userList = new ArrayList<>();
        when(userRepository.findAll()).thenReturn(userList);

        // when
        userService.getAll();

        // then
        verify(userRepository, times(1)).findAll();
    }


    @Test
    public void testGetById() {
        // given
        User user = new User(1, "Имя", "email@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when
        userService.getById(1);

        // then
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testUpdate() {
        // given
        UserDto userDto = new UserDto(1, "Новое имя", "new_email@example.com");
        User existingUser = new User(1, "Старое имя", "email@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        // when
        userService.update(userDto);

        // then

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testDelete() {
        // when

        userService.delete(1);

        // then
        verify(userRepository, times(1)).deleteById(1L);
    }

}
