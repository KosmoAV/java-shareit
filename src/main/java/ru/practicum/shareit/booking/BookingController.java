package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.exception.DataBadRequestException;
import ru.practicum.shareit.group.OnPost;

import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseBookingDto addBooking(@RequestHeader("X-Sharer-User-Id") @Positive long bookerId,
                                         @RequestBody @Validated(OnPost.class) CreateBookingDto createBookingDto) {

        log.info("Call 'addBooking': bookerId = {}, {}", bookerId, createBookingDto);

        createBookingDto.setBookerId(bookerId);
        createBookingDto.setStatus(Status.WAITING);

        return bookingService.addBooking(createBookingDto);
    }

    @PatchMapping("/{bookingId}")
    ResponseBookingDto approveBooking(@RequestHeader("X-Sharer-User-Id") @Positive long ownerId,
                                      @PathVariable @Positive long bookingId,
                                      @RequestParam boolean approved) {

        return bookingService.approveBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    ResponseBookingDto getBooking(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                  @PathVariable @Positive long bookingId) {

        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping()
    List<ResponseBookingDto> getAllBooking(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                           @RequestParam(defaultValue = "ALL") String state) {

        State enumState;

        try {
            enumState = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new DataBadRequestException("Unknown state: " + state, e.getMessage());
        }

        return bookingService.getAllBooking(userId, enumState);
    }

    @GetMapping("/owner")
    List<ResponseBookingDto> getAllOwnerBooking(@RequestHeader("X-Sharer-User-Id") @Positive long ownerId,
                                           @RequestParam(defaultValue = "ALL") String state) {

        State enumState;

        try {
            enumState = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new DataBadRequestException("Unknown state: " + state, e.getMessage());
        }

        return bookingService.getAllOwnerBooking(ownerId, enumState);
    }
}
