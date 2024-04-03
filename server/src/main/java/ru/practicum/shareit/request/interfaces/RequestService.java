package ru.practicum.shareit.request.interfaces;

import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.ResponseRequestDto;
import java.util.List;

public interface RequestService {

    public ResponseRequestDto addRequest(long requestorId, CreateRequestDto createRequestDto);

    public List<ResponseRequestDto> getUserRequests(long userId);

    public List<ResponseRequestDto> getAllRequests(long userId, Integer from, Integer size);

    ResponseRequestDto getRequestById(long userId, long requestId);
}
