package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.DataBadRequestException;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.interfaces.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTests {
    private BookingService bookingService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;

    @BeforeEach
    public void setUp() {

        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
    }

    @Test
    public void addBookingTest() {

        when(userRepository.findById(0L)).thenReturn(Optional.empty());
        User user = makeUser(1L, "Alex", "Alex@mail.net");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        when(itemRepository.findById(0L)).thenReturn(Optional.empty());
        Item item = makeItem(1L, "Name", "Ho4u sleeeep!",
                true, 2L, null);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        when(bookingRepository.save(any())).thenAnswer(invocationOnMock -> {
            Booking booking = invocationOnMock.getArgument(0, Booking.class);
            booking.setId(1L);
            return booking;
        });

        LocalDateTime dateTime = LocalDateTime.now();
        CreateBookingDto createBookingDto = makeCreateBookingDto(null, dateTime.plusDays(1), dateTime.plusDays(2),
                item.getId(), user.getId(), null);

        ResponseBookingDto responseBookingDto = bookingService.addBooking(createBookingDto);
        assertThat(responseBookingDto, notNullValue());
        assertThat(responseBookingDto.getId(), equalTo(1L));
        assertThat(responseBookingDto.getStart(), equalTo(createBookingDto.getStart()));
        assertThat(responseBookingDto.getEnd(), equalTo(createBookingDto.getEnd()));
        assertThat(responseBookingDto.getItem(), notNullValue());
        assertThat(responseBookingDto.getItem().getId(), equalTo(item.getId()));
        assertThat(responseBookingDto.getItem().getName(), equalTo(item.getName()));
        assertThat(responseBookingDto.getItem().getDescription(), equalTo(item.getDescription()));
        assertThat(responseBookingDto.getItem().getAvailable(), equalTo(item.getAvailable()));
        assertThat(responseBookingDto.getItem().getOwner(), equalTo(item.getOwnerId()));
        assertThat(responseBookingDto.getItem().getRequestId(), equalTo(item.getRequest()));
        assertThat(responseBookingDto.getBooker(), notNullValue());
        assertThat(responseBookingDto.getBooker().getId(), equalTo(user.getId()));
        assertThat(responseBookingDto.getBooker().getName(), equalTo(user.getName()));
        assertThat(responseBookingDto.getBooker().getEmail(), equalTo(user.getEmail()));
        assertThat(responseBookingDto.getStatus(), equalTo(Status.WAITING));

        createBookingDto.setItemId(0L);
        assertThrows(DataNotFoundException.class, () -> bookingService.addBooking(createBookingDto));

        createBookingDto.setItemId(1L);
        createBookingDto.setBookerId(0L);
        assertThrows(DataNotFoundException.class, () -> bookingService.addBooking(createBookingDto));

        createBookingDto.setBookerId(1L);
        createBookingDto.setStart(dateTime.plusDays(40));
        assertThrows(DataBadRequestException.class, () -> bookingService.addBooking(createBookingDto));

        createBookingDto.setStart(createBookingDto.getEnd());
        assertThrows(DataBadRequestException.class, () -> bookingService.addBooking(createBookingDto));

        createBookingDto.setStart(createBookingDto.getEnd().minusDays(1));
        item.setAvailable(false);
        assertThrows(DataBadRequestException.class, () -> bookingService.addBooking(createBookingDto));

        item.setAvailable(true);
        item.setOwnerId(user.getId());
        assertThrows(DataNotFoundException.class, () -> bookingService.addBooking(createBookingDto));
    }

    @Test
    public void approveBookingTest() {

        User user = makeUser(1L, "Alex", "Alex@mail.net");

        Item item = makeItem(1L, "Name", "Ho4u sleeeep!",
                true, 2L, null);

        when(bookingRepository.findById(0L)).thenReturn(Optional.empty());
        LocalDateTime dateTime = LocalDateTime.now();
        Booking booking = makeBooking(1L, dateTime.plusDays(1), dateTime.plusDays(2), item, user, Status.WAITING);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        when(bookingRepository.save(any()))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, Booking.class));

        ResponseBookingDto responseBookingDto = bookingService.approveBooking(2L, booking.getId(), true);
        assertThat(responseBookingDto, notNullValue());
        assertThat(responseBookingDto.getId(), equalTo(booking.getId()));
        assertThat(responseBookingDto.getStart(), equalTo(booking.getStart()));
        assertThat(responseBookingDto.getEnd(), equalTo(booking.getEnd()));
        assertThat(responseBookingDto.getItem(), notNullValue());
        assertThat(responseBookingDto.getItem().getId(), equalTo(item.getId()));
        assertThat(responseBookingDto.getItem().getName(), equalTo(item.getName()));
        assertThat(responseBookingDto.getItem().getDescription(), equalTo(item.getDescription()));
        assertThat(responseBookingDto.getItem().getAvailable(), equalTo(item.getAvailable()));
        assertThat(responseBookingDto.getItem().getOwner(), equalTo(item.getOwnerId()));
        assertThat(responseBookingDto.getItem().getRequestId(), equalTo(item.getRequest()));
        assertThat(responseBookingDto.getBooker(), notNullValue());
        assertThat(responseBookingDto.getBooker().getId(), equalTo(user.getId()));
        assertThat(responseBookingDto.getBooker().getName(), equalTo(user.getName()));
        assertThat(responseBookingDto.getBooker().getEmail(), equalTo(user.getEmail()));
        assertThat(responseBookingDto.getStatus(), equalTo(Status.APPROVED));

        assertThrows(DataBadRequestException.class,
                () -> bookingService.approveBooking(2L, booking.getId(), true));

        booking.setStatus(Status.WAITING);
        responseBookingDto = bookingService.approveBooking(2L, booking.getId(), false);
        assertThat(responseBookingDto, notNullValue());
        assertThat(responseBookingDto.getId(), equalTo(booking.getId()));
        assertThat(responseBookingDto.getStatus(), equalTo(Status.REJECTED));

        booking.setStatus(Status.WAITING);
        assertThrows(DataNotFoundException.class,
                () -> bookingService.approveBooking(2L, 0L, true));

        assertThrows(DataNotFoundException.class,
                () -> bookingService.approveBooking(1L, booking.getId(), true));
    }

    @Test
    public void getBookingTest() {

        User user = makeUser(1L, "Alex", "Alex@mail.net");

        Item item = makeItem(1L, "Name", "Ho4u sleeeep!",
                true, 2L, null);

        when(bookingRepository.findById(0L)).thenReturn(Optional.empty());
        LocalDateTime dateTime = LocalDateTime.now();
        Booking booking = makeBooking(1L, dateTime.plusDays(1), dateTime.plusDays(2), item, user, Status.WAITING);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        ResponseBookingDto responseBookingDto = bookingService.getBooking(booking.getId(), booking.getId());
        assertThat(responseBookingDto, notNullValue());
        assertThat(responseBookingDto.getId(), equalTo(booking.getId()));
        assertThat(responseBookingDto.getStart(), equalTo(booking.getStart()));
        assertThat(responseBookingDto.getEnd(), equalTo(booking.getEnd()));
        assertThat(responseBookingDto.getItem(), notNullValue());
        assertThat(responseBookingDto.getItem().getId(), equalTo(item.getId()));
        assertThat(responseBookingDto.getItem().getName(), equalTo(item.getName()));
        assertThat(responseBookingDto.getItem().getDescription(), equalTo(item.getDescription()));
        assertThat(responseBookingDto.getItem().getAvailable(), equalTo(item.getAvailable()));
        assertThat(responseBookingDto.getItem().getOwner(), equalTo(item.getOwnerId()));
        assertThat(responseBookingDto.getItem().getRequestId(), equalTo(item.getRequest()));
        assertThat(responseBookingDto.getBooker(), notNullValue());
        assertThat(responseBookingDto.getBooker().getId(), equalTo(user.getId()));
        assertThat(responseBookingDto.getBooker().getName(), equalTo(user.getName()));
        assertThat(responseBookingDto.getBooker().getEmail(), equalTo(user.getEmail()));
        assertThat(responseBookingDto.getStatus(), equalTo(Status.WAITING));

        responseBookingDto = bookingService.getBooking(item.getOwnerId(), booking.getId());
        assertThat(responseBookingDto, notNullValue());
        assertThat(responseBookingDto.getId(), equalTo(booking.getId()));
        assertThat(responseBookingDto.getStart(), equalTo(booking.getStart()));
        assertThat(responseBookingDto.getEnd(), equalTo(booking.getEnd()));
        assertThat(responseBookingDto.getItem(), notNullValue());
        assertThat(responseBookingDto.getItem().getId(), equalTo(item.getId()));
        assertThat(responseBookingDto.getItem().getName(), equalTo(item.getName()));
        assertThat(responseBookingDto.getItem().getDescription(), equalTo(item.getDescription()));
        assertThat(responseBookingDto.getItem().getAvailable(), equalTo(item.getAvailable()));
        assertThat(responseBookingDto.getItem().getOwner(), equalTo(item.getOwnerId()));
        assertThat(responseBookingDto.getItem().getRequestId(), equalTo(item.getRequest()));
        assertThat(responseBookingDto.getBooker(), notNullValue());
        assertThat(responseBookingDto.getBooker().getId(), equalTo(user.getId()));
        assertThat(responseBookingDto.getBooker().getName(), equalTo(user.getName()));
        assertThat(responseBookingDto.getBooker().getEmail(), equalTo(user.getEmail()));
        assertThat(responseBookingDto.getStatus(), equalTo(Status.WAITING));

        assertThrows(DataNotFoundException.class, () -> bookingService.getBooking(user.getId(), 0L));

        assertThrows(DataNotFoundException.class, () -> bookingService.getBooking(4L, booking.getId()));
    }

    @Test
    public void getAllBookingTest() {

        when(userRepository.findById(0L)).thenReturn(Optional.empty());
        User user = makeUser(1L, "Alex", "Alex@mail.net");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        Item item = makeItem(1L, "Name", "Ho4u sleeeep!",
                true, 2L, null);

        LocalDateTime dateTime = LocalDateTime.now();
        Booking booking = makeBooking(1L, dateTime.plusDays(1), dateTime.plusDays(2), item, user, Status.WAITING);
        when(bookingRepository.findAll()).thenReturn(List.of(booking));
        when(bookingRepository.findByBookerIdWithCurrentState(anyLong(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(bookingRepository.findByBookerIdWithPastState(anyLong(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(bookingRepository.findByBookerIdWithFutureState(anyLong(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(bookingRepository.findByBookerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<ResponseBookingDto> list = bookingService.getAllBooking(user.getId(), null, null, null);
        assertThat(list, notNullValue());
        assertThat(list, hasSize(1));
        assertThat(list.get(0), notNullValue());
        assertThat(list.get(0).getId(), equalTo(booking.getId()));
        assertThat(list.get(0).getStart(), equalTo(booking.getStart()));
        assertThat(list.get(0).getEnd(), equalTo(booking.getEnd()));
        assertThat(list.get(0).getItem(), notNullValue());
        assertThat(list.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(list.get(0).getItem().getName(), equalTo(item.getName()));
        assertThat(list.get(0).getItem().getDescription(), equalTo(item.getDescription()));
        assertThat(list.get(0).getItem().getAvailable(), equalTo(item.getAvailable()));
        assertThat(list.get(0).getItem().getOwner(), equalTo(item.getOwnerId()));
        assertThat(list.get(0).getItem().getRequestId(), equalTo(item.getRequest()));
        assertThat(list.get(0).getBooker(), notNullValue());
        assertThat(list.get(0).getBooker().getId(), equalTo(user.getId()));
        assertThat(list.get(0).getBooker().getName(), equalTo(user.getName()));
        assertThat(list.get(0).getBooker().getEmail(), equalTo(user.getEmail()));
        assertThat(list.get(0).getStatus(), equalTo(Status.WAITING));

        list = bookingService.getAllBooking(user.getId(), State.CURRENT.toString(), 0, 50);
        assertThat(list, notNullValue());
        assertThat(list, hasSize(1));
        assertThat(list.get(0), notNullValue());
        assertThat(list.get(0).getId(), equalTo(booking.getId()));

        list = bookingService.getAllBooking(user.getId(), State.PAST.toString(), 0, 50);
        assertThat(list, notNullValue());
        assertThat(list, hasSize(1));
        assertThat(list.get(0), notNullValue());
        assertThat(list.get(0).getId(), equalTo(booking.getId()));

        list = bookingService.getAllBooking(user.getId(), State.FUTURE.toString(), 0, 50);
        assertThat(list, notNullValue());
        assertThat(list, hasSize(1));
        assertThat(list.get(0), notNullValue());
        assertThat(list.get(0).getId(), equalTo(booking.getId()));

        list = bookingService.getAllBooking(user.getId(), State.WAITING.toString(), 0, 50);
        assertThat(list, notNullValue());
        assertThat(list, hasSize(1));
        assertThat(list.get(0), notNullValue());
        assertThat(list.get(0).getId(), equalTo(booking.getId()));

        list = bookingService.getAllBooking(user.getId(), State.REJECTED.toString(), 0, 50);
        assertThat(list, notNullValue());
        assertThat(list, hasSize(1));
        assertThat(list.get(0), notNullValue());
        assertThat(list.get(0).getId(), equalTo(booking.getId()));

        list = bookingService.getAllBooking(user.getId(), State.ALL.toString(), 0, 50);
        assertThat(list, notNullValue());
        assertThat(list, hasSize(1));
        assertThat(list.get(0), notNullValue());
        assertThat(list.get(0).getId(), equalTo(booking.getId()));

        assertThrows(DataBadRequestException.class,
                () -> bookingService.getAllBooking(user.getId(), "Nothing", 0, 50));

        assertThrows(DataBadRequestException.class,
                () -> bookingService.getAllBooking(user.getId(), State.ALL.toString(), -1, 50));

        assertThrows(DataBadRequestException.class,
                () -> bookingService.getAllBooking(user.getId(), State.ALL.toString(), 0, 0));

        assertThrows(DataNotFoundException.class,
                () -> bookingService.getAllBooking(0L, State.ALL.toString(), 0, 1));
    }

    @Test
    public void getAllOwnerBookingTest() {

        when(userRepository.findById(0L)).thenReturn(Optional.empty());
        User user = makeUser(1L, "Alex", "Alex@mail.net");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        Item item = makeItem(1L, "Name", "Ho4u sleeeep!",
                true, 2L, null);

        LocalDateTime dateTime = LocalDateTime.now();
        Booking booking = makeBooking(1L, dateTime.plusDays(1), dateTime.plusDays(2), item, user, Status.WAITING);
        when(bookingRepository.findByItemOwnerIdWithCurrentState(anyLong(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(bookingRepository.findByItemOwnerIdWithPastState(anyLong(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(bookingRepository.findByItemOwnerIdWithFutureState(anyLong(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<ResponseBookingDto> list = bookingService.getAllOwnerBooking(user.getId(),
                                            State.CURRENT.toString(), null, null);
        assertThat(list, notNullValue());
        assertThat(list, hasSize(1));
        assertThat(list.get(0), notNullValue());
        assertThat(list.get(0).getId(), equalTo(booking.getId()));
        assertThat(list.get(0).getStart(), equalTo(booking.getStart()));
        assertThat(list.get(0).getEnd(), equalTo(booking.getEnd()));
        assertThat(list.get(0).getItem(), notNullValue());
        assertThat(list.get(0).getItem().getId(), equalTo(item.getId()));
        assertThat(list.get(0).getItem().getName(), equalTo(item.getName()));
        assertThat(list.get(0).getItem().getDescription(), equalTo(item.getDescription()));
        assertThat(list.get(0).getItem().getAvailable(), equalTo(item.getAvailable()));
        assertThat(list.get(0).getItem().getOwner(), equalTo(item.getOwnerId()));
        assertThat(list.get(0).getItem().getRequestId(), equalTo(item.getRequest()));
        assertThat(list.get(0).getBooker(), notNullValue());
        assertThat(list.get(0).getBooker().getId(), equalTo(user.getId()));
        assertThat(list.get(0).getBooker().getName(), equalTo(user.getName()));
        assertThat(list.get(0).getBooker().getEmail(), equalTo(user.getEmail()));
        assertThat(list.get(0).getStatus(), equalTo(Status.WAITING));

        list = bookingService.getAllOwnerBooking(user.getId(), State.PAST.toString(), 0, 50);
        assertThat(list, notNullValue());
        assertThat(list, hasSize(1));
        assertThat(list.get(0), notNullValue());
        assertThat(list.get(0).getId(), equalTo(booking.getId()));

        list = bookingService.getAllOwnerBooking(user.getId(), State.FUTURE.toString(), 0, 50);
        assertThat(list, notNullValue());
        assertThat(list, hasSize(1));
        assertThat(list.get(0), notNullValue());
        assertThat(list.get(0).getId(), equalTo(booking.getId()));

        list = bookingService.getAllOwnerBooking(user.getId(), State.WAITING.toString(), 0, 50);
        assertThat(list, notNullValue());
        assertThat(list, hasSize(1));
        assertThat(list.get(0), notNullValue());
        assertThat(list.get(0).getId(), equalTo(booking.getId()));

        list = bookingService.getAllOwnerBooking(user.getId(), State.REJECTED.toString(), 0, 50);
        assertThat(list, notNullValue());
        assertThat(list, hasSize(1));
        assertThat(list.get(0), notNullValue());
        assertThat(list.get(0).getId(), equalTo(booking.getId()));

        list = bookingService.getAllOwnerBooking(user.getId(), State.ALL.toString(), 0, 50);
        assertThat(list, notNullValue());
        assertThat(list, hasSize(1));
        assertThat(list.get(0), notNullValue());
        assertThat(list.get(0).getId(), equalTo(booking.getId()));

        assertThrows(DataBadRequestException.class,
                () -> bookingService.getAllOwnerBooking(user.getId(), "Nothing", 0, 50));

        assertThrows(DataBadRequestException.class,
                () -> bookingService.getAllOwnerBooking(user.getId(), State.ALL.toString(), -1, 50));

        assertThrows(DataBadRequestException.class,
                () -> bookingService.getAllOwnerBooking(user.getId(), State.ALL.toString(), 0, 0));

        assertThrows(DataNotFoundException.class,
                () -> bookingService.getAllOwnerBooking(0L, State.ALL.toString(), 0, 1));
    }

    private ItemDto makeItemDto(Long id, String name, String description,
                                Boolean available, Long owner, Long requestId) {

        ItemDto dto = new ItemDto();
        dto.setId(id);
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);
        dto.setOwner(owner);
        dto.setRequestId(requestId);

        return dto;
    }

    private UserDto makeUserDto(String name, String email) {

        UserDto dto = new UserDto();
        dto.setEmail(email);
        dto.setName(name);

        return dto;
    }

    private CreateCommentDto makeCreateCommentDto(Long id, String text, Long itemId, Long authorId) {

        CreateCommentDto dto = new CreateCommentDto();
        dto.setId(id);
        dto.setText(text);
        dto.setItemId(itemId);
        dto.setAuthorId(authorId);

        return dto;
    }

    private CreateBookingDto makeCreateBookingDto(Long id, LocalDateTime start, LocalDateTime end,
                                    Long itemId, Long bookerId, Status status) {

        CreateBookingDto dto = new CreateBookingDto();
        dto.setId(id);
        dto.setStart(start);
        dto.setEnd(end);
        dto.setItemId(itemId);
        dto.setBookerId(bookerId);
        dto.setStatus(status);

        return dto;
    }

    private Item makeItem(Long id, String name, String description,
                          Boolean available, Long ownerId, Request request) {

        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwnerId(ownerId);
        item.setRequest(request);

        return item;
    }

    private User makeUser(Long id, String name, String email) {

        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setName(name);

        return user;
    }

    private Booking makeBooking(Long id, LocalDateTime start, LocalDateTime end,
                                Item item, User booker, Status status) {

        Booking booking = new Booking();
        booking.setId(id);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(status);

        return booking;
    }
}
