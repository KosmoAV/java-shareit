package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateRequestDto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class RequestController {

    private final RequestClient requestClient;

    @PostMapping
    ResponseEntity<Object> addRequest(@RequestHeader("X-Sharer-User-Id") @Positive long requestorId,
                                      @RequestBody @Validated CreateRequestDto createRequestDto) {

        log.info("Call 'addRequest': User id = {}, {}", requestorId, createRequestDto);

        return requestClient.addRequest(createRequestDto, requestorId);
    }

    @GetMapping
    ResponseEntity<Object> getUserRequests(@RequestHeader("X-Sharer-User-Id") @Positive long userId) {

        log.info("Call 'getUserRequests': User id = {}", userId);

        return requestClient.getUserRequests(userId);
    }

    @GetMapping("/all")
    ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                            @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                            @RequestParam(defaultValue = "50") @Min(1) @Max(100) Integer size) {

        log.info("Call 'getAllRequests': Owner id = {}, from = {}, size = {}", userId, from, size);

        return requestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                    @PathVariable @Positive long requestId) {

        log.info("Call 'getRequestById': User id = {}, request id = {}", userId, requestId);

        return requestClient.getRequestById(userId, requestId);
    }
}
