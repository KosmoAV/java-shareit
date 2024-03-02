package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemRequestDto {

    private long id;

    private String description;

    private long requestor;

    private long created;
}
