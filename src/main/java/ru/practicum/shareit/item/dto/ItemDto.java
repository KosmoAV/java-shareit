package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.group.OnPatch;
import ru.practicum.shareit.group.OnPost;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
public class ItemDto {

    private Long id;

    @NotNull(groups = OnPost.class)
    @NotBlank(groups = OnPost.class)
    @Size(groups = {OnPost.class, OnPatch.class}, max = 32)
    private String name;

    @NotNull(groups = OnPost.class)
    @NotBlank(groups = OnPost.class)
    @Size(groups = {OnPost.class, OnPatch.class}, max = 64)
    private String description;

    @NotNull(groups = OnPost.class)
    private Boolean available;

    @Positive(groups = {OnPost.class, OnPatch.class})
    private Long owner;

    private Long requestId;
}
