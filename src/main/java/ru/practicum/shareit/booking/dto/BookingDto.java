package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingDto {

    private long id;

    private long start;

    private long end;

    private long item;

    private long booker;

    private BookingDto.Status status;

    enum Status {
        WAITING,
        APPROVED,
        REJECTED,
        CANCELED
    }
}
