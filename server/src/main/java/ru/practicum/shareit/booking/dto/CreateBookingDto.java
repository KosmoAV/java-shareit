package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

@Data
public class CreateBookingDto {

    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private long itemId;

    private long bookerId;

    private Status status;
}
