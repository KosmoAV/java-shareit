package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.DataBadRequestException;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.interfaces.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ResponseBookingDto addBooking(CreateBookingDto createBookingDto) {

        createBookingDto.setStatus(Status.WAITING);

        validDates(createBookingDto.getStart(), createBookingDto.getEnd());

        Item item = getItemIfAvailable(createBookingDto.getItemId());
        if (item.getOwnerId() == createBookingDto.getBookerId()) {
            throw new DataNotFoundException("Owner can not booking item");
        }

        User booker = getUser(createBookingDto.getBookerId());

        Booking booking = BookingMapper.toBooking(createBookingDto, item, booker);

        return BookingMapper.toResponseBookingDto(bookingRepository.save(booking));
    }

    @Override
    public ResponseBookingDto approveBooking(long ownerId, long bookingId, boolean approved) {

        Booking booking = getBooking(bookingId);

        if (booking.getItem().getOwnerId() != ownerId) {
            throw new DataNotFoundException("Invalid owner ID");
        }

        if (booking.getStatus() != Status.WAITING) {
            throw new DataBadRequestException("Status already changed");
        }

        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        return BookingMapper.toResponseBookingDto(bookingRepository.save(booking));
    }

    @Override
    public ResponseBookingDto getBooking(long userId, long bookingId) {

        Booking booking = getBooking(bookingId);

        if (booking.getBooker().getId() == userId || booking.getItem().getOwnerId() == userId) {
            return BookingMapper.toResponseBookingDto(booking);
        }

        throw new DataNotFoundException("Invalid user ID");
    }

    @Override
    public List<ResponseBookingDto> getAllBooking(long bookerId, String stringState) {

        State state = getState(stringState);

        getUser(bookerId);

        List<Booking> bookingList;

        if (state == null) {
            bookingList = bookingRepository.findAll();
            return BookingMapper.toResponseBookingDto(bookingList);
        }

        switch (state) {
            case CURRENT:
                bookingList = bookingRepository.findByBookerIdWithCurrentState(bookerId);
            break;
            case PAST:
                bookingList = bookingRepository.findByBookerIdWithPastState(bookerId);
            break;
            case FUTURE:
                bookingList = bookingRepository.findByBookerIdWithFutureState(bookerId);
            break;
            case WAITING:
                bookingList = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, Status.WAITING);
            break;
            case REJECTED:
                bookingList = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, Status.REJECTED);
            break;
            default:
                bookingList = bookingRepository.findByBookerIdOrderByStartDesc(bookerId);
            break;
        }

        return BookingMapper.toResponseBookingDto(bookingList);
    }

    @Override
    public List<ResponseBookingDto> getAllOwnerBooking(long ownerId, String stringState) {

        State state = getState(stringState);

        getUser(ownerId);

        List<Booking> bookingList;

        switch (state) {
            case CURRENT:
                bookingList = bookingRepository.findByItemOwnerIdWithCurrentState(ownerId);
                break;
            case PAST:
                bookingList = bookingRepository.findByItemOwnerIdWithPastState(ownerId);
                break;
            case FUTURE:
                bookingList = bookingRepository.findByItemOwnerIdWithFutureState(ownerId);
                break;
            case WAITING:
                bookingList = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, Status.WAITING);
                break;
            case REJECTED:
                bookingList = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, Status.REJECTED);
                break;
            default:
                bookingList = bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
                break;
        }

        return BookingMapper.toResponseBookingDto(bookingList);
    }

    private void validDates(LocalDateTime start, LocalDateTime end) {

        if (end.isBefore(start)) {
            throw new DataBadRequestException("End time is before start time");
        }

        if (start.equals(end)) {
            throw new DataBadRequestException("Start time equals end time");
        }
    }

    private User getUser(long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User with id = " + userId + " not found"));
    }

    private Item getItemIfAvailable(long itemId) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new DataNotFoundException("Item with id = " + itemId + " not found"));

        if (!item.getAvailable()) {
            throw new DataBadRequestException("Item id = " + itemId + " is not available");
        }

        return item;
    }

    private Booking getBooking(long bookingId) {

        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new DataNotFoundException("Booking id = " + bookingId + " not found"));
    }

    private State getState(String state) {

        try {
            return State.valueOf(state);

        } catch (IllegalArgumentException e) {
            throw new DataBadRequestException("Unknown state: " + state, e.getMessage());
        }
    }
}
