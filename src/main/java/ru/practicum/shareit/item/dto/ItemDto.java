package ru.practicum.shareit.item.dto;

import lombok.Data;


@Data
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long owner;
    private Long request;

    public ItemDto(String name, String description, Boolean available, Long request) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.request = request;
    }
}
