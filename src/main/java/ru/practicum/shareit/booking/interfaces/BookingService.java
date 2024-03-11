package ru.practicum.shareit.booking.interfaces;

import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {

    public ResponseBookingDto addBooking(CreateBookingDto createBookingDto);

    public ResponseBookingDto approveBooking(long ownerId, long bookingId, boolean approved);

    public ResponseBookingDto getBooking(long userId, long bookingId);

    public List<ResponseBookingDto> getAllBooking(long bookerId, State state);

    public List<ResponseBookingDto> getAllOwnerBooking(long ownerId, State state);
}
