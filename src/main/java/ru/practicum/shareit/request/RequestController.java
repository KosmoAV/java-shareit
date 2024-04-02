package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.ResponseRequestDto;
import ru.practicum.shareit.request.interfaces.RequestService;

import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    ResponseRequestDto addRequest(@RequestHeader("X-Sharer-User-Id") @Positive long requestorId,
                                  @RequestBody @Validated CreateRequestDto createRequestDto) {

        log.info("Call 'addRequest': User id = {}, {}", requestorId, createRequestDto);

        return requestService.addRequest(requestorId, createRequestDto);
    }

    @GetMapping
    List<ResponseRequestDto> getUserRequests(@RequestHeader("X-Sharer-User-Id") @Positive long userId) {

        log.info("Call 'getUserRequests': User id = {}", userId);

        return requestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    List<ResponseRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                            @RequestParam(required = false) Integer from,
                                            @RequestParam(required = false) Integer size) {

        log.info("Call 'getAllRequests': Owner id = {}, from = {}, size = {}", userId, from, size);

        return requestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    ResponseRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                    @PathVariable @Positive long requestId) {

        log.info("Call 'getRequestById': User id = {}, request id = {}", userId, requestId);

        return requestService.getRequestById(userId, requestId);
    }
}
