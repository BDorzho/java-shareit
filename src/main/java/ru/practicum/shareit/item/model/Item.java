package ru.practicum.shareit.item.model;

import lombok.Data;

import ru.practicum.shareit.request.dto.ItemRequestDto;


@Data
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long owner;
    private ItemRequestDto request;

    public Item(String name, String description, Boolean available, Long owner) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
    }
}
