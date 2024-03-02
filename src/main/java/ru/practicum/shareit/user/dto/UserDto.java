package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.group.OnPatch;
import ru.practicum.shareit.group.OnPost;

import javax.validation.constraints.*;

@Data
@Builder
public class UserDto {

    @Positive
    private Long id;

    @NotNull(groups = OnPost.class)
    @NotBlank(groups = OnPost.class)
    @Size(groups = {OnPost.class, OnPatch.class}, max = 16)
    private String name;

    @NotNull(groups = OnPost.class)
    @NotBlank(groups = OnPost.class)
    @Email(groups = {OnPost.class, OnPatch.class})
    private String email;
}
