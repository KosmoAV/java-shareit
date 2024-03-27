package ru.practicum.shareit.booking.interfaces;

import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;

import java.util.List;

public interface BookingService {

    public ResponseBookingDto addBooking(CreateBookingDto createBookingDto);

    public ResponseBookingDto approveBooking(long ownerId, long bookingId, boolean approved);

    public ResponseBookingDto getBooking(long userId, long bookingId);

    public List<ResponseBookingDto> getAllBooking(long bookerId, String stringState, Integer from, Integer size);

    public List<ResponseBookingDto> getAllOwnerBooking(long ownerId, String stringState, Integer from, Integer size);
}
