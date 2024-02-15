package ru.practicum.shareit.request.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@Builder
public class ItemRequest {

    @Min(1)
    private long id;

    @NotBlank
    @Size(max = 128)
    private String description;

    @Min(1)
    private long requestor;

    @Positive
    private long created;
}
