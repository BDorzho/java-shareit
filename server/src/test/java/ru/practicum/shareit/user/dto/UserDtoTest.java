package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class UserDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testJsonSerialization() throws Exception {

        UserDto user = new UserDto(1, "Иванов", "ivanov@example.com");

        String jsonString = objectMapper.writeValueAsString(user);

        assertEquals("{\"id\":1,\"name\":\"Иванов\",\"email\":\"ivanov@example.com\"}", jsonString);
    }

    @Test
    public void testJsonDeserialization() throws Exception {

        String jsonString = "{\"id\":1,\"name\":\"Иванов\",\"email\":\"ivanov@example.com\"}";

        UserDto user = objectMapper.readValue(jsonString, UserDto.class);

        assertEquals(1, user.getId());
        assertEquals("Иванов", user.getName());
        assertEquals("ivanov@example.com", user.getEmail());
    }
}
