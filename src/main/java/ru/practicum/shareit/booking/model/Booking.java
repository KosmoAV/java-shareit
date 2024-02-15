package ru.practicum.shareit.booking.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Booking {

    private long id;

    private long start;

    private long end;

    private long item;

    private long booker;

    private Status status;

    enum Status {
        WAITING,
        APPROVED,
        REJECTED,
        CANCELED
    }
}
