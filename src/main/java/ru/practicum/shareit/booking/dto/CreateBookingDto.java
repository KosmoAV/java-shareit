package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.group.OnPatch;
import ru.practicum.shareit.group.OnPost;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
public class CreateBookingDto {

    private long id;

    @NotNull(groups = OnPost.class)
    @FutureOrPresent(groups = {OnPost.class, OnPatch.class})
    private LocalDateTime start;

    @NotNull(groups = OnPost.class)
    @Future(groups = {OnPost.class, OnPatch.class})
    private LocalDateTime end;

    @NotNull
    @Positive
    private long itemId;

    private long bookerId;

    private Status status;
}
