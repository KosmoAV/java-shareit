package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.ResponseRequestDto;
import ru.practicum.shareit.request.interfaces.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    ResponseRequestDto addRequest(@RequestHeader("X-Sharer-User-Id") long requestorId,
                                  @RequestBody CreateRequestDto createRequestDto) {

        log.info("Call 'addRequest': User id = {}, {}", requestorId, createRequestDto);

        return requestService.addRequest(requestorId, createRequestDto);
    }

    @GetMapping
    List<ResponseRequestDto> getUserRequests(@RequestHeader("X-Sharer-User-Id") long userId) {

        log.info("Call 'getUserRequests': User id = {}", userId);

        return requestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    List<ResponseRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @RequestParam Integer from,
                                            @RequestParam Integer size) {

        log.info("Call 'getAllRequests': Owner id = {}, from = {}, size = {}", userId, from, size);

        return requestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    ResponseRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @PathVariable long requestId) {

        log.info("Call 'getRequestById': User id = {}, request id = {}", userId, requestId);

        return requestService.getRequestById(userId, requestId);
    }
}
