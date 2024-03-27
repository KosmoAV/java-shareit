package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataBadRequestException;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ResponseRequestItemDto;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.ResponseRequestDto;
import ru.practicum.shareit.request.interfaces.RequestRepository;
import ru.practicum.shareit.request.interfaces.RequestService;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.interfaces.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ResponseRequestDto addRequest(long requestorId, CreateRequestDto createRequestDto) {

        User requestor = getUserById(requestorId);

        Request request = RequestMapper.toRequest(createRequestDto, requestor);

        return RequestMapper.toResponseRequestDto(requestRepository.save(request), null);
    }

    @Override
    public List<ResponseRequestDto> getUserRequests(long userId) {

        getUserById(userId);

        List<Request> requests = requestRepository.findByRequestorId(userId);

        return makeResponseRequestDtoList(requests);
    }

    @Override
    public List<ResponseRequestDto> getAllRequests(long userId, Integer from, Integer size) {

        if (from == null || size == null) {
            List<Request> requests = requestRepository.findByOtherRequestorId(userId);
            return makeResponseRequestDtoList(requests);
        }

        if (size < 1) {
            throw new DataBadRequestException("Parameter 'size' in method getAllRequests mast be > 0");
        }

        if (from < 0) {
            throw new DataBadRequestException("Parameter 'from' in method getAllRequests mast be >= 0");
        }

        getUserById(userId);

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        List<Request> requests = requestRepository.findByOtherRequestorId(userId, page)
                .getContent();

        return makeResponseRequestDtoList(requests);
    }

    @Override
    public ResponseRequestDto getRequestById(long userId, long requestId) {

        getUserById(userId);
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new DataNotFoundException("Request with id = " + requestId + " not found"));

        List<Item> items = itemRepository.findByRequestIds(List.of(request.getId()));

        return RequestMapper.toResponseRequestDto(request,
                items.stream()
                        .map(ItemMapper::toResponseRequestItemDto)
                        .collect(Collectors.toList()));
    }

    private User getUserById(long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User with id = " + userId + " not found"));
    }

    private List<ResponseRequestDto> makeResponseRequestDtoList(List<Request> requests) {

        List<Long> requestIds = requests.stream()
                .map(Request::getId)
                .collect(Collectors.toList());

        List<Item> items = itemRepository.findByRequestIds(requestIds);

        Map<Long, List<ResponseRequestItemDto>> itemDtoMap = new HashMap<>();

        for (Item item : items) {
            long requestId = item.getRequest().getId();
            if (itemDtoMap.containsKey(requestId)) {
                itemDtoMap.get(requestId).add(ItemMapper.toResponseRequestItemDto(item));
            } else {
                itemDtoMap.put(requestId, new ArrayList<>(List.of(ItemMapper.toResponseRequestItemDto(item))));
            }
        }

        return requests.stream()
                .map(request -> RequestMapper.toResponseRequestDto(request, itemDtoMap.get(request.getId())))
                .collect(Collectors.toList());
    }
}
