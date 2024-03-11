package ru.practicum.shareit.item.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CreateCommentDto {

    private Long id;

    @NotNull()
    @NotBlank()
    @Size(max = 256)
    private String text;

    private Long itemId;

    private Long authorId;
}
