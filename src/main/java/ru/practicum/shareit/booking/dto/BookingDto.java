package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Booking;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

@Data
@Builder
public class BookingDto {

    @Min(1)
    private long id;

    @Positive
    private long start;

    @Positive
    private long end;

    @Min(1)
    private long item;

    @Min(1)
    private long booker;

    private BookingDto.Status status;

    enum Status {
        WAITING,
        APPROVED,
        REJECTED,
        CANCELED
    }
}
