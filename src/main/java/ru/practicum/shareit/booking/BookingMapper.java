package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {
    public static ResponseBookingDto toResponseBookingDto(Booking booking) {

        if (booking == null) {
            throw new IllegalArgumentException("Parameter booking in method toResponseBookingDto must be non-null");
        }

        ResponseBookingDto responseBookingDto = new ResponseBookingDto();

        responseBookingDto.setId(booking.getId());
        responseBookingDto.setStart(booking.getStart());
        responseBookingDto.setEnd(booking.getEnd());
        responseBookingDto.setItem(ItemMapper.toItemDto(booking.getItem()));
        responseBookingDto.setBooker(UserMapper.toUserDto(booking.getBooker()));
        responseBookingDto.setStatus(booking.getStatus());

        return responseBookingDto;
    }

    public static List<ResponseBookingDto> toResponseBookingDto(List<Booking> bookingList) {

        if (bookingList == null) {
            throw new IllegalArgumentException("Parameter bookingList in method toResponseBookingDto must be non-null");
        }

        return bookingList.stream()
                .map(BookingMapper::toResponseBookingDto)
                .collect(Collectors.toList());
    }

    public static Booking toBooking(CreateBookingDto createBookingDto, Item item, User booker) {

        if (createBookingDto == null || item == null || booker == null) {
            throw new IllegalArgumentException("Parameters in method toBooking must be non-null");
        }

        Booking booking = new Booking();

        booking.setId(createBookingDto.getId());
        booking.setStart(createBookingDto.getStart());
        booking.setEnd(createBookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(createBookingDto.getStatus());

        return booking;
    }
}
