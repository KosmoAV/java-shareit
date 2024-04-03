package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.group.OnPost;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader("X-Sharer-User-Id") @Positive long bookerId,
                                             @RequestBody @Validated(OnPost.class) CreateBookingDto createBookingDto) {

        log.info("Call 'addBooking': bookerId = {}, {}", bookerId, createBookingDto);

        createBookingDto.setBookerId(bookerId);

        return bookingClient.addBooking(bookerId, createBookingDto);
    }

    @PatchMapping("/{bookingId}")
    ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") @Positive long ownerId,
                                      @PathVariable @Positive long bookingId,
                                      @RequestParam Boolean approved) {

        log.info("Call 'approveBooking': ownerId = {}, bookingId = {}, approved = {}", ownerId, bookingId, approved);

        return bookingClient.approveBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                  @PathVariable @Positive long bookingId) {

        log.info("Call 'getBooking': userId = {}, bookingId = {}", userId, bookingId);

        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping()
    ResponseEntity<Object> getAllBooking(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                           @RequestParam(defaultValue = "ALL") String state,
                                           @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                           @RequestParam(defaultValue = "50") @Min(1) @Max(100) Integer size) {

        log.info("Call 'getAllBooking': userId = {}, state = {}, from = {}, size = {}", userId, state, from, size);

        return bookingClient.getAllBooking(userId, state, from, size);
    }

    @GetMapping("/owner")
    ResponseEntity<Object> getAllOwnerBooking(@RequestHeader("X-Sharer-User-Id") @Positive long ownerId,
                                                @RequestParam(defaultValue = "ALL") String state,
                                                @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                @RequestParam(defaultValue = "50") @Min(1) @Max(100) Integer size) {

        log.info("Call 'getAllBooking': ownerId = {}, state = {}, from = {}, size = {}", ownerId, state, from, size);

        return bookingClient.getAllOwnerBooking(ownerId, state, from, size);
    }
}
