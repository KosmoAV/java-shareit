package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.RequestServiceImpl;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.ResponseRequestDto;
import ru.practicum.shareit.request.interfaces.RequestRepository;
import ru.practicum.shareit.request.interfaces.RequestService;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.interfaces.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTests {
    private RequestService requestService;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {

        requestService = new RequestServiceImpl(requestRepository, itemRepository, userRepository);
    }

    @Test
    public void addRequestTest() {

        when(userRepository.findById(0L)).thenReturn(Optional.ofNullable(null));

        User user = makeUser(1L, "Alex", "Alex@mail.net");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        LocalDateTime dateTime = LocalDateTime.of(2024, 5, 6, 12, 8, 45);
        when(requestRepository.save(any())).thenAnswer(invocationOnMock -> {
            Request request = invocationOnMock.getArgument(0, Request.class);
            request.setId(1L);
            request.setCreated(dateTime);
            return request;
        });

        CreateRequestDto createRequestDto = makeCreateRequestDto("Description!");

        assertThrows(DataNotFoundException.class, () -> requestService.addRequest(0L, createRequestDto));

        ResponseRequestDto responseRequestDto = requestService.addRequest(user.getId(), createRequestDto);
        assertThat(responseRequestDto.getId(), equalTo(1L));
        assertThat(responseRequestDto.getDescription(), equalTo(createRequestDto.getDescription()));
        assertThat(responseRequestDto.getCreated(), equalTo(dateTime));
        assertThat(responseRequestDto.getItems(), empty());
    }

    @Test
    public void getUserRequestsTest() {

        List<User> users = List.of(
                makeUser(0L, null, null),
                makeUser(1L, "Name1", "Email1@pipec.ru"),
                makeUser(2L, "Name2", "Email2@email.net")
        );

        when(userRepository.findById(anyLong())).thenAnswer(invocationOnMock -> {
            Long id = invocationOnMock.getArgument(0, Long.class);

            if (id < 1 || id > users.size() - 1) {
                return Optional.empty();
            } else {
                return Optional.of(users.get(id.intValue()));
            }
        });


        LocalDateTime dateTime = LocalDateTime.of(2024, 5, 21, 4, 32, 25);

        List<Request> requests = List.of(
                makeRequest(0L, null, null, users.get(0)),
                makeRequest(1L, "Ustal", dateTime, users.get(2)),
                makeRequest(2L, "Ochen", dateTime.plusDays(1), users.get(2))
        );

        when(requestRepository.findByRequestorId(anyLong())).thenAnswer(invocationOnMock -> {
            Long id = invocationOnMock.getArgument(0, Long.class);

            return requests.stream()
                    .filter(request -> request.getRequestor().getId() == id)
                    .collect(Collectors.toList());
        });

        List<Item> items = List.of(
                makeItem(0L, null, null, true, 10L, requests.get(0)),
                makeItem(1L, "Item1", "Description1", true, 10L, requests.get(1)),
                makeItem(2L, "Item2", "Description2", true, 10L, requests.get(2))
        );

        when(itemRepository.findByRequestIds(anyList())).thenAnswer(invocationOnMock -> {
            List<Long> ids = invocationOnMock.getArgument(0, List.class);

            return items.stream()
                    .filter(item -> ids.contains(item.getRequest().getId()))
                    .collect(Collectors.toList());
        });

        assertThrows(DataNotFoundException.class, () -> requestService.getUserRequests(0L));

        List<ResponseRequestDto> responseRequestDtoList = requestService.getUserRequests(2);
        assertThat(responseRequestDtoList, notNullValue());
        assertThat(responseRequestDtoList, hasSize(2));
        assertThat(responseRequestDtoList.get(0), notNullValue());
        assertThat(responseRequestDtoList.get(0).getId(), equalTo(requests.get(1).getId()));
        assertThat(responseRequestDtoList.get(0).getDescription(), equalTo(requests.get(1).getDescription()));
        assertThat(responseRequestDtoList.get(0).getCreated(), equalTo(requests.get(1).getCreated()));
        assertThat(responseRequestDtoList.get(0).getItems(), notNullValue());
        assertThat(responseRequestDtoList.get(0).getItems().size(), equalTo(1));
        assertThat(responseRequestDtoList.get(0).getItems().get(0).getId(), equalTo(items.get(1).getId()));
        assertThat(responseRequestDtoList.get(1), notNullValue());
        assertThat(responseRequestDtoList.get(1).getId(), equalTo(requests.get(2).getId()));
        assertThat(responseRequestDtoList.get(1).getDescription(), equalTo(requests.get(2).getDescription()));
        assertThat(responseRequestDtoList.get(1).getCreated(), equalTo(requests.get(2).getCreated()));
        assertThat(responseRequestDtoList.get(1).getItems(), notNullValue());
        assertThat(responseRequestDtoList.get(1).getItems().size(), equalTo(1));
        assertThat(responseRequestDtoList.get(1).getItems().get(0).getId(), equalTo(items.get(2).getId()));

        responseRequestDtoList = requestService.getUserRequests(1);
        assertThat(responseRequestDtoList, notNullValue());
        assertThat(responseRequestDtoList, empty());
    }

    @Test
    public void getAllRequestsTest() {

        List<User> users = List.of(
                makeUser(0L, null, null),
                makeUser(1L, "Name1", "Email1@pipec.ru"),
                makeUser(2L, "Name2", "Email2@email.net")
        );

        when(userRepository.findById(anyLong())).thenAnswer(invocationOnMock -> {
            Long id = invocationOnMock.getArgument(0, Long.class);

            if (id < 1 || id > users.size() - 1) {
                return Optional.empty();
            } else {
                return Optional.of(users.get(id.intValue()));
            }
        });


        LocalDateTime dateTime = LocalDateTime.of(2024, 5, 21, 4, 32, 25);

        List<Request> requests = List.of(
                makeRequest(1L, "Ustal", dateTime, users.get(2)),
                makeRequest(2L, "Ochen", dateTime.plusDays(1), users.get(2))
        );

        when(requestRepository.findByOtherRequestorId(anyLong(), any())).thenAnswer(invocationOnMock -> {
            Long id = invocationOnMock.getArgument(0, Long.class);

            return new PageImpl<>(requests.stream()
                    .filter(request -> request.getRequestor().getId() != id)
                    .collect(Collectors.toList()));
        });

        List<Item> items = List.of(
                makeItem(1L, "Item1", "Description1", true, 10L, requests.get(0)),
                makeItem(2L, "Item2", "Description2", true, 10L, requests.get(1))
        );

        when(itemRepository.findByRequestIds(anyList())).thenAnswer(invocationOnMock -> {
            List<Long> ids = invocationOnMock.getArgument(0, List.class);

            return items.stream()
                    .filter(item -> ids.contains(item.getRequest().getId()))
                    .collect(Collectors.toList());
        });

        assertThrows(DataNotFoundException.class, () -> requestService.getAllRequests(0L, 0, 32));

        List<ResponseRequestDto> responseRequestDtoList = requestService.getAllRequests(1, 0, 32);
        assertThat(responseRequestDtoList, notNullValue());
        assertThat(responseRequestDtoList, hasSize(2));
        assertThat(responseRequestDtoList.get(0), notNullValue());
        assertThat(responseRequestDtoList.get(0).getId(), equalTo(requests.get(0).getId()));
        assertThat(responseRequestDtoList.get(0).getDescription(), equalTo(requests.get(0).getDescription()));
        assertThat(responseRequestDtoList.get(0).getCreated(), equalTo(requests.get(0).getCreated()));
        assertThat(responseRequestDtoList.get(0).getItems(), notNullValue());
        assertThat(responseRequestDtoList.get(0).getItems().size(), equalTo(1));
        assertThat(responseRequestDtoList.get(0).getItems().get(0).getId(), equalTo(items.get(0).getId()));
        assertThat(responseRequestDtoList.get(1), notNullValue());
        assertThat(responseRequestDtoList.get(1).getId(), equalTo(requests.get(1).getId()));
        assertThat(responseRequestDtoList.get(1).getDescription(), equalTo(requests.get(1).getDescription()));
        assertThat(responseRequestDtoList.get(1).getCreated(), equalTo(requests.get(1).getCreated()));
        assertThat(responseRequestDtoList.get(1).getItems(), notNullValue());
        assertThat(responseRequestDtoList.get(1).getItems().size(), equalTo(1));
        assertThat(responseRequestDtoList.get(1).getItems().get(0).getId(), equalTo(items.get(1).getId()));

        responseRequestDtoList = requestService.getAllRequests(2, 0, 32);
        assertThat(responseRequestDtoList, notNullValue());
        assertThat(responseRequestDtoList, empty());
    }

    @Test
    public void getRequestByIdTest() {

        when(userRepository.findById(0L)).thenReturn(Optional.empty());
        User user = makeUser(1L, "Alex", "Alex@mail.net");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        when(requestRepository.findById(0L)).thenReturn(Optional.empty());
        Request request = makeRequest(1L, "RRRR", LocalDateTime.now(), user);
        when(requestRepository.findById(request.getId())).thenReturn(Optional.of(request));

        List<Item> items = List.of(
                makeItem(1L, "Item1", "Description1", true, 10L, new Request()),
                makeItem(2L, "Item2", "Description2", true, 10L, request)
        );
        when(itemRepository.findByRequestIds(anyList())).thenAnswer(invocationOnMock -> {
            List<Long> ids = invocationOnMock.getArgument(0, List.class);

            return items.stream()
                    .filter(item -> ids.contains(item.getRequest().getId()))
                    .collect(Collectors.toList());
        });

        assertThrows(DataNotFoundException.class, () -> requestService.getRequestById(0L, 1L));
        assertThrows(DataNotFoundException.class, () -> requestService.getRequestById(1L, 0L));

        ResponseRequestDto responseRequestDto = requestService.getRequestById(1L, 1L);
        assertThat(responseRequestDto, notNullValue());
        assertThat(responseRequestDto.getId(), equalTo(request.getId()));
        assertThat(responseRequestDto.getDescription(), equalTo(request.getDescription()));
        assertThat(responseRequestDto.getCreated(), notNullValue());
        assertThat(responseRequestDto.getItems(), notNullValue());
        assertThat(responseRequestDto.getItems(), hasSize(1));
        assertThat(responseRequestDto.getItems().get(0), notNullValue());
        assertThat(responseRequestDto.getItems().get(0).getId(), equalTo(items.get(1).getId()));
    }

    private CreateRequestDto makeCreateRequestDto(String description) {

        CreateRequestDto dto = new CreateRequestDto();
        dto.setDescription(description);

        return dto;
    }

    private UserDto makeUserDto(String name, String email) {

        UserDto dto = new UserDto();
        dto.setEmail(email);
        dto.setName(name);

        return dto;
    }

    private User makeUser(Long id, String name, String email) {

        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setName(name);

        return user;
    }

    private Request makeRequest(Long id, String description, LocalDateTime dateTime, User requestor) {

        Request request = new Request();
        request.setId(id);
        request.setDescription(description);
        request.setCreated(dateTime);
        request.setRequestor(requestor);

        return request;
    }

    private Item makeItem(Long id, String name, String description, Boolean available, Long ownerId, Request request) {

        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwnerId(ownerId);
        item.setRequest(request);

        return item;
    }
}
