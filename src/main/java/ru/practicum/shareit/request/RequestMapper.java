package ru.practicum.shareit.request;

import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.ResponseCommentDto;
import ru.practicum.shareit.item.dto.ResponseRequestItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.ResponseRequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RequestMapper {

    public static Request toRequest(CreateRequestDto createRequestDto, User requestor) {

        if (createRequestDto == null) {
            throw new IllegalArgumentException("Parameter createRequestDto in method toRequest must be non-null");
        }

        Request request = new Request();

        request.setDescription(createRequestDto.getDescription());
        request.setCreated(LocalDateTime.now());
        request.setRequestor(requestor);

        return request;
    }

    public static ResponseRequestDto toResponseRequestDto(Request request, List<ResponseRequestItemDto> items) {

        if (request == null) {
            throw new IllegalArgumentException("Parameter request in method toResponseRequestDto must be non-null");
        }

        ResponseRequestDto responseRequestDto = new ResponseRequestDto();

        responseRequestDto.setId(request.getId());
        responseRequestDto.setDescription(request.getDescription());
        responseRequestDto.setCreated(request.getCreated());

        responseRequestDto.setItems(Objects.requireNonNullElseGet(items, ArrayList::new));

        return responseRequestDto;
    }
}
