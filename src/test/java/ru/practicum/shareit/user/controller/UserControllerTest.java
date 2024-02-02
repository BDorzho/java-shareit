package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.error.ErrorHandler;
import ru.practicum.shareit.validation.exception.NotFoundException;
import ru.practicum.shareit.validation.exception.ValidationException;


import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private MockMvc mvc;
    @InjectMocks
    private UserController controller;
    @Mock
    private UserService userService;
    private final ObjectMapper mapper = new ObjectMapper();
    private List<UserDto> userList;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(ErrorHandler.class)
                .build();

        userList = Arrays.asList(
                new UserDto(1, "John", "john@example.com"),
                new UserDto(2, "Jane", "jane@example.com"),
                new UserDto(3, "Bob", "bob@example.com"),
                new UserDto(4, "Alice", "alice@example.com"));
    }

    @Test
    public void createUser_Success() throws Exception {
        // given
        UserDto userDto = userList.get(0);

        // when
        when(userService.create(any(UserDto.class))).thenReturn(userDto);

        // then
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));


        verify(userService, times(1)).create(any(UserDto.class));
    }

    @Test
    public void createUser_Fail_DuplicateEmail() throws Exception {
        // given
        UserDto userDto = new UserDto(20, "Ivan", "jane@example.com");

        // when
        when(userService.create(any(UserDto.class))).thenThrow(new ValidationException("Email already exists"));

        // then
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).create(any(UserDto.class));
    }

    @Test
    public void createUser_Fail_NoEmail() throws Exception {
        // given
        UserDto userDto = new UserDto(30, "Bob", "");

        // then
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).create(any(UserDto.class));
    }

    @Test
    public void createUser_Fail_InvalidEmail() throws Exception {
        // given
        UserDto userDto = new UserDto(40, "Alice", "invalidemail");


        // then
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).create(any(UserDto.class));
    }

    @Test
    public void updateUser_Success() throws Exception {
        // given
        UserDto userDto = new UserDto(1, "Updated Name", "john@example.com");

        // when
        when(userService.update(eq(1L), any(UserDto.class))).thenReturn(userDto);

        // then
        mvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Name")))
                .andExpect(jsonPath("$.email", is("john@example.com")));

        verify(userService, times(1)).update(eq(1L), any(UserDto.class));
    }

    @Test
    public void updateEmail_Success() throws Exception {
        // given
        UserDto userDto = new UserDto(1, "Updated Name", "updated@example.com");

        // when
        when(userService.update(eq(1L), any(UserDto.class))).thenReturn(userDto);

        // then
        mvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Name")))
                .andExpect(jsonPath("$.email", is("updated@example.com")));

        verify(userService, times(1)).update(eq(1L), any(UserDto.class));
    }

    @Test
    public void updateWithSameEmail_Success() throws Exception {
        // given
        UserDto updatedUserDto = new UserDto(1, "Updated Name", "updated@example.com");

        // when
        when(userService.update(eq(1L), any(UserDto.class))).thenReturn(updatedUserDto);

        // then
        mvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updatedUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Name")))
                .andExpect(jsonPath("$.email", is("updated@example.com")));

        verify(userService, times(1)).update(eq(1L), any(UserDto.class));
    }

    @Test
    public void nameUpdateFailed_EmailExists() throws Exception {
        // given
        UserDto updatedUserDto = new UserDto(1, "Updated Name", "jane@example.com");

        // when
        when(userService.update(eq(1L), any(UserDto.class)))
                .thenThrow(new ValidationException("Email already exists"));

        // then
        mvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updatedUserDto)))
                .andExpect(status().isBadRequest());


        verify(userService, times(1)).update(eq(1L), any(UserDto.class));
    }

    @Test
    public void getUser_ReturnsUpdatedUser() throws Exception {
        // given
        UserDto updatedUserDto = new UserDto(1, "Updated Name", "updated@example.com");

        // when
        when(userService.getById(1L)).thenReturn(updatedUserDto);

        // then
        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Name")))
                .andExpect(jsonPath("$.email", is("updated@example.com")));

        verify(userService, times(1)).getById(1L);
    }

    @Test
    public void getUser5_ReturnsNull() throws Exception {
        // when
        when(userService.getById(5L)).thenThrow(new NotFoundException("Пользователь не найден"));

        // then
        mvc.perform(get("/users/5"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getById(5L);
    }

    @Test
    public void getUser10_ReturnsUnknown() throws Exception {
        // when
        when(userService.getById(10L)).thenThrow(new NotFoundException("Пользователь не найден"));

        // then
        mvc.perform(get("/users/10"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getById(10L);
    }

    @Test
    public void deleteUser3_Success() throws Exception {
        // when
        doNothing().when(userService).delete(3L);

        // then
        mvc.perform(delete("/users/3"))
                .andExpect(status().isOk());

        verify(userService, times(1)).delete(3L);
    }

    @Test
    public void createUser_AfterDelete_Success() throws Exception {
        // given
        UserDto userDto = userList.get(3);

        // when
        when(userService.create(any(UserDto.class))).thenReturn(userDto);

        // then
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userService, times(1)).create(any(UserDto.class));
    }

    @Test
    public void getAllUsers_Success() throws Exception {
        // when
        when(userService.getAll()).thenReturn(userList);

        // then
        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("John")))
                .andExpect(jsonPath("$[0].email", is("john@example.com")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Jane")))
                .andExpect(jsonPath("$[1].email", is("jane@example.com")))
                .andExpect(jsonPath("$[2].id", is(3)))
                .andExpect(jsonPath("$[2].name", is("Bob")))
                .andExpect(jsonPath("$[2].email", is("bob@example.com")))
                .andExpect(jsonPath("$[3].id", is(4)))
                .andExpect(jsonPath("$[3].name", is("Alice")))
                .andExpect(jsonPath("$[3].email", is("alice@example.com")));

        verify(userService, times(1)).getAll();
    }


}
