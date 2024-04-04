package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ResponseRequestItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ResponseRequestDto {

    private long id;

    private String description;

    private LocalDateTime created;

    List<ResponseRequestItemDto> items;
}
