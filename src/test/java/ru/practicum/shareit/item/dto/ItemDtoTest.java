package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;


import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    public void testJsonSerialization() throws Exception {

        ItemDto item = new ItemDto(1L, "Название", "Описание", true, 1L, null);


        String result = json.write(item).getJson();


        assertThat(result).isEqualTo("{\"id\":1,\"name\":\"Название\",\"description\":\"Описание\",\"available\":true,\"owner\":1,\"requestId\":null}");
    }

    @Test
    public void testJsonDeserialization() throws Exception {

        ItemDto item = new ItemDto(1L, "Название", "Описание", true, 1L, null);
        String content = "{\"id\":1,\"name\":\"Название\",\"description\":\"Описание\",\"available\":true,\"owner\":1,\"requestId\":null}";


        ItemDto result = json.parse(content).getObject();


        assertThat(result).isEqualTo(item);
    }

}
