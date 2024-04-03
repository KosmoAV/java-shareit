package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class CreateCommentDto {

    private Long id;

    private String text;

    private Long itemId;

    private Long authorId;
}
