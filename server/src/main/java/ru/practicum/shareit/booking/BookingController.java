package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.interfaces.BookingService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseBookingDto addBooking(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                         @RequestBody CreateBookingDto createBookingDto) {

        log.info("Call 'addBooking': bookerId = {}, {}", bookerId, createBookingDto);

        createBookingDto.setBookerId(bookerId);

        return bookingService.addBooking(createBookingDto);
    }

    @PatchMapping("/{bookingId}")
    ResponseBookingDto approveBooking(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                      @PathVariable long bookingId,
                                      @RequestParam boolean approved) {

        log.info("Call 'approveBooking': ownerId = {}, bookingId = {}, approved = {}", ownerId, bookingId, approved);

        return bookingService.approveBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    ResponseBookingDto getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @PathVariable long bookingId) {

        log.info("Call 'getBooking': userId = {}, bookingId = {}", userId, bookingId);

        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping()
    List<ResponseBookingDto> getAllBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestParam String state,
                                           @RequestParam Integer from,
                                           @RequestParam Integer size) {

        log.info("Call 'getAllBooking': userId = {}, state = {}, from = {}, size = {}", userId, state, from, size);

        return bookingService.getAllBooking(userId, state, from, size);
    }

    @GetMapping("/owner")
    List<ResponseBookingDto> getAllOwnerBooking(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                @RequestParam String state,
                                                @RequestParam Integer from,
                                                @RequestParam Integer size) {

        log.info("Call 'getAllBooking': ownerId = {}, state = {}, from = {}, size = {}", ownerId, state, from, size);

        return bookingService.getAllOwnerBooking(ownerId, state, from, size);
    }
}
