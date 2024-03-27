package ru.practicum.shareit.request.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CreateRequestDto {

    @NotNull()
    @NotBlank()
    @Size(max = 128)
    private String description;
}
