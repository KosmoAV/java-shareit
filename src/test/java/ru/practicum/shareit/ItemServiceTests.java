package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.DataBadRequestException;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseCommentDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.interfaces.CommentRepository;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.interfaces.RequestRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.interfaces.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTests {

    private ItemService itemService;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;

    @BeforeEach
    public void setUp() {

        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository,
                commentRepository, requestRepository);
    }

    @Test
    public void addItemTest() {

        when(userRepository.existsById(0L)).thenReturn(false);
        User user = makeUser(1L, "Alex", "Alex@mail.net");
        when(userRepository.existsById(user.getId())).thenReturn(true);

        when(requestRepository.findById(0L)).thenReturn(Optional.empty());
        Request request = makeRequest(1L, "Description", LocalDateTime.now(), user);
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        ItemDto itemDto = makeItemDto(1L, "Name", "Ho4u sleeeep!",
                true, 0L, request.getId());
        when(itemRepository.save(any())).thenAnswer(invocationOnMock -> {
            return invocationOnMock.getArgument(0, Item.class);
        });

        assertThrows(DataNotFoundException.class, () -> itemService.addItem(itemDto));

        itemDto.setOwner(1L);
        ItemDto saveItemDto = itemService.addItem(itemDto);
        assertThat(saveItemDto, notNullValue());
        assertThat(saveItemDto.getId(), equalTo(itemDto.getId()));
        assertThat(saveItemDto.getName(), equalTo(itemDto.getName()));
        assertThat(saveItemDto.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(saveItemDto.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(saveItemDto.getOwner(), equalTo(itemDto.getOwner()));
        assertThat(saveItemDto.getRequestId(), equalTo(request.getId()));

        itemDto.setRequestId(0L);
        assertThrows(DataNotFoundException.class, () -> itemService.addItem(itemDto));

        itemDto.setRequestId(null);
        saveItemDto = itemService.addItem(itemDto);
        assertThat(saveItemDto, notNullValue());
        assertThat(saveItemDto.getId(), equalTo(itemDto.getId()));
        assertThat(saveItemDto.getName(), equalTo(itemDto.getName()));
        assertThat(saveItemDto.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(saveItemDto.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(saveItemDto.getOwner(), equalTo(itemDto.getOwner()));
        assertThat(saveItemDto.getRequestId(), equalTo(null));
    }

    @Test
    public void updateItemTest() {

        when(userRepository.existsById(0L)).thenReturn(false);
        User user = makeUser(1L, "Alex", "Alex@mail.net");
        when(userRepository.existsById(user.getId())).thenReturn(true);

        when(itemRepository.findById(0L)).thenReturn(Optional.empty());

        Request request = makeRequest(1L, "Request", LocalDateTime.now(), user);

        Item item = makeItem(1L, "Name", "Ho4u sleeeep!",
                true, 1L, request);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        when(itemRepository.save(any())).thenAnswer(invocationOnMock -> {
            return invocationOnMock.getArgument(0, Item.class);
        });

        ItemDto itemDto = makeItemDto(0L, "NewName", "New GGG", false, 0L, null);

        assertThrows(DataNotFoundException.class, () -> itemService.updateItem(itemDto));

        itemDto.setOwner(1L);
        assertThrows(DataNotFoundException.class, () -> itemService.updateItem(itemDto));

        itemDto.setId(1L);
        ItemDto savaItemDto = itemService.updateItem(itemDto);
        assertThat(savaItemDto, notNullValue());
        assertThat(savaItemDto.getId(), equalTo(itemDto.getId()));
        assertThat(savaItemDto.getName(), equalTo(itemDto.getName()));
        assertThat(savaItemDto.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(savaItemDto.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(savaItemDto.getOwner(), equalTo(itemDto.getOwner()));
        assertThat(savaItemDto.getRequestId(), equalTo(item.getRequest().getId()));
    }

    @Test
    public void getItemTest() {

        when(userRepository.existsById(0L)).thenReturn(false);
        User user1 = makeUser(1L, "Alex", "Alex@mail.net");
        User user2 = makeUser(2L, "Ivan", "Ivan@mail.net");
        when(userRepository.existsById(user1.getId())).thenReturn(true);
        when(userRepository.existsById(user2.getId())).thenReturn(true);

        when(itemRepository.findById(0L)).thenReturn(Optional.empty());

        Request request = makeRequest(1L, "Request", LocalDateTime.now(), user1);

        Item item = makeItem(1L, "Name", "Ho4u sleeeep!",
                true, user1.getId(), request);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        LocalDateTime dateTime = LocalDateTime.now();
        Booking lastBooking = makeBooking(1L, dateTime.minusDays(10), dateTime.minusDays(5),
                item, user1, Status.APPROVED);
        Booking nextBooking = makeBooking(2L, dateTime.plusDays(1), dateTime.plusDays(4),
                item, user1, Status.APPROVED);
        when(bookingRepository.findLastBookingItem(anyLong(), anyInt())).thenReturn(lastBooking);
        when(bookingRepository.findNextBookingItem(anyLong(), anyInt())).thenReturn(nextBooking);

        List<Comment> comments = List.of(
                makeComment(1L, "Comment1", item, user1, dateTime),
                makeComment(2L, "Comment2", item, user1, dateTime)
        );
        when(commentRepository.findByItemId(anyLong())).thenReturn(comments);

        assertThrows(DataNotFoundException.class, () -> itemService.getItem(0L, item.getId()));
        assertThrows(DataNotFoundException.class, () -> itemService.getItem(user1.getId(), 0L));

        ResponseItemDto responseItemDto = itemService.getItem(user1.getId(), item.getId());
        assertThat(responseItemDto, notNullValue());
        assertThat(responseItemDto.getId(), equalTo(item.getId()));
        assertThat(responseItemDto.getName(), equalTo(item.getName()));
        assertThat(responseItemDto.getDescription(), equalTo(item.getDescription()));
        assertThat(responseItemDto.getAvailable(), equalTo(item.getAvailable()));
        assertThat(responseItemDto.getRequest(), equalTo(request.getId()));
        assertThat(responseItemDto.getOwner(), equalTo(item.getOwnerId()));
        assertThat(responseItemDto.getComments(), notNullValue());
        assertThat(responseItemDto.getComments(), hasSize(2));
        assertThat(responseItemDto.getComments().get(0), notNullValue());
        assertThat(responseItemDto.getComments().get(0).getId(), equalTo(comments.get(0).getId()));
        assertThat(responseItemDto.getComments().get(1), notNullValue());
        assertThat(responseItemDto.getComments().get(1).getId(), equalTo(comments.get(1).getId()));
        assertThat(responseItemDto.getLastBooking(), notNullValue());
        assertThat(responseItemDto.getLastBooking(), notNullValue());

        responseItemDto = itemService.getItem(user2.getId(), item.getId());
        assertThat(responseItemDto.getLastBooking(), nullValue());
        assertThat(responseItemDto.getLastBooking(), nullValue());
    }

    @Test
    public void getItemsTest() {

        when(userRepository.existsById(0L)).thenReturn(false);
        User user = makeUser(1L, "Alex", "Alex@mail.net");
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);

        when(itemRepository.findByOwnerId(2L)).thenReturn(new ArrayList<>());

        List<Item> items = List.of(
                makeItem(1L, "Name1", "Ho4u sleeeep!",
                        true, user.getId(), null),
                makeItem(2L, "Name2", "I want kill JAVA!!!",
                true, user.getId(), null)
        );
        when(itemRepository.findByOwnerId(user.getId())).thenReturn(items);

        LocalDateTime dateTime = LocalDateTime.now();
        List<Booking> bookings = List.of(
                makeBooking(1L, dateTime.minusDays(10), dateTime.minusDays(5),
                        items.get(0), user, Status.APPROVED),
                makeBooking(2L, dateTime.plusDays(1), dateTime.plusDays(4),
                        items.get(0), user, Status.APPROVED),
                makeBooking(3L, dateTime.minusDays(45), dateTime.minusDays(35),
                        items.get(1), user, Status.APPROVED),
                makeBooking(4L, dateTime.plusDays(5), dateTime.plusDays(10),
                        items.get(1), user, Status.APPROVED)
        );

        List<Long> itemsIds = items.stream().map(Item::getId).collect(Collectors.toList());

        when(bookingRepository.findByItemIdAndStatusOrderByStart(itemsIds, Status.APPROVED)).thenReturn(bookings);

        List<Comment> comments = List.of(
                makeComment(1L, "CommentItem1", items.get(0), user, dateTime),
                makeComment(2L, "CommentItem2", items.get(1), user, dateTime)
        );
        when(commentRepository.findByItemsId(anyList())).thenReturn(comments);

        assertThrows(DataNotFoundException.class, () -> itemService.getItems(0L));

        List<ResponseItemDto> responseItemDtoList = itemService.getItems(user.getId());
        assertThat(responseItemDtoList, notNullValue());
        assertThat(responseItemDtoList, hasSize(2));
        assertThat(responseItemDtoList.get(0), notNullValue());
        assertThat(responseItemDtoList.get(0).getId(), equalTo(items.get(0).getId()));
        assertThat(responseItemDtoList.get(0).getName(), equalTo(items.get(0).getName()));
        assertThat(responseItemDtoList.get(0).getDescription(), equalTo(items.get(0).getDescription()));
        assertThat(responseItemDtoList.get(0).getAvailable(), equalTo(items.get(0).getAvailable()));
        assertThat(responseItemDtoList.get(0).getOwner(), equalTo(items.get(0).getOwnerId()));
        assertThat(responseItemDtoList.get(0).getRequest(), equalTo(items.get(0).getRequest()));
        assertThat(responseItemDtoList.get(0).getLastBooking(), notNullValue());
        assertThat(responseItemDtoList.get(0).getNextBooking(), notNullValue());
        assertThat(responseItemDtoList.get(0).getComments(), notNullValue());
        assertThat(responseItemDtoList.get(0).getComments(), hasSize(1));
        assertThat(responseItemDtoList.get(0).getComments().get(0), notNullValue());
        assertThat(responseItemDtoList.get(0).getComments().get(0).getId(), equalTo(comments.get(0).getId()));
        assertThat(responseItemDtoList.get(1), notNullValue());
        assertThat(responseItemDtoList.get(1).getId(), equalTo(items.get(1).getId()));
        assertThat(responseItemDtoList.get(1).getName(), equalTo(items.get(1).getName()));
        assertThat(responseItemDtoList.get(1).getDescription(), equalTo(items.get(1).getDescription()));
        assertThat(responseItemDtoList.get(1).getAvailable(), equalTo(items.get(1).getAvailable()));
        assertThat(responseItemDtoList.get(1).getOwner(), equalTo(items.get(1).getOwnerId()));
        assertThat(responseItemDtoList.get(1).getRequest(), equalTo(items.get(1).getRequest()));
        assertThat(responseItemDtoList.get(1).getLastBooking(), notNullValue());
        assertThat(responseItemDtoList.get(1).getNextBooking(), notNullValue());
        assertThat(responseItemDtoList.get(1).getComments(), notNullValue());
        assertThat(responseItemDtoList.get(1).getComments(), hasSize(1));
        assertThat(responseItemDtoList.get(1).getComments().get(0), notNullValue());
        assertThat(responseItemDtoList.get(1).getComments().get(0).getId(), equalTo(comments.get(1).getId()));

        responseItemDtoList = itemService.getItems(2L);
        assertThat(responseItemDtoList, notNullValue());
        assertThat(responseItemDtoList, empty());
    }

    @Test
    public void searchItemsTest() {

        when(userRepository.existsById(0L)).thenReturn(false);
        User user = makeUser(1L, "Alex", "Alex@mail.net");
        when(userRepository.existsById(user.getId())).thenReturn(true);

        when(itemRepository.findByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue("0", "0"))
                .thenReturn(new ArrayList<>());
        Item item = makeItem(1L, "Name", "Ho4u sleeeep!",
                true, user.getId(), null);
        when(itemRepository.findByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue("a", "a"))
                .thenReturn(List.of(item));

        assertThrows(DataNotFoundException.class, () -> itemService.searchItems(0L, "a"));

        List<ItemDto> itemDtoList = itemService.searchItems(user.getId(), "a");
        assertThat(itemDtoList, notNullValue());
        assertThat(itemDtoList, hasSize(1));
        assertThat(itemDtoList.get(0), notNullValue());
        assertThat(itemDtoList.get(0).getId(), equalTo(item.getId()));
        assertThat(itemDtoList.get(0).getName(), equalTo(item.getName()));
        assertThat(itemDtoList.get(0).getDescription(), equalTo(item.getDescription()));
        assertThat(itemDtoList.get(0).getAvailable(), equalTo(item.getAvailable()));
        assertThat(itemDtoList.get(0).getOwner(), equalTo(item.getOwnerId()));
        assertThat(itemDtoList.get(0).getRequestId(), equalTo(item.getRequest()));

        itemDtoList = itemService.searchItems(user.getId(), "0");
        assertThat(itemDtoList, notNullValue());
        assertThat(itemDtoList, empty());
    }

    @Test
    public void addCommentTest() {

        when(userRepository.findById(0L)).thenReturn(Optional.empty());
        User user = makeUser(1L, "Alex", "Alex@mail.net");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        when(itemRepository.findById(0L)).thenReturn(Optional.empty());
        Item item = makeItem(1L, "Name", "Ho4u sleeeep!",
                true, user.getId(), null);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        when(bookingRepository.existsByItemIdAndBookerIdAndEndBefore(same(item.getId()), same(user.getId()), any()))
                .thenReturn(true);

        when(commentRepository.save(any())).thenAnswer(invocationOnMock -> {
            Comment comment = invocationOnMock.getArgument(0, Comment.class);
            comment.setId(1L);
            return comment;
        });

        CreateCommentDto createCommentDto = makeCreateCommentDto(null, "Text", item.getId(), 0L);

        assertThrows(DataNotFoundException.class, () -> itemService.addComment(createCommentDto));

        createCommentDto.setItemId(0L);
        createCommentDto.setAuthorId(1L);
        assertThrows(DataNotFoundException.class, () -> itemService.addComment(createCommentDto));

        createCommentDto.setItemId(1L);
        ResponseCommentDto responseCommentDto = itemService.addComment(createCommentDto);
        assertThat(responseCommentDto, notNullValue());
        assertThat(responseCommentDto.getId(), equalTo(1L));
        assertThat(responseCommentDto.getText(), equalTo(createCommentDto.getText()));
        assertThat(responseCommentDto.getAuthorName(), equalTo(user.getName()));
        assertThat(responseCommentDto.getCreated(), notNullValue());
    }

    @Test
    public void addCommentExceptionTest() {

        User user = makeUser(1L, "Alex", "Alex@mail.net");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        Item item = makeItem(1L, "Name", "Ho4u sleeeep!",
                true, user.getId(), null);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        when(bookingRepository.existsByItemIdAndBookerIdAndEndBefore(same(item.getId()), same(user.getId()), any()))
                .thenReturn(false);

        CreateCommentDto createCommentDto = makeCreateCommentDto(null, "Text", item.getId(), user.getId());

        assertThrows(DataBadRequestException.class, () -> itemService.addComment(createCommentDto));
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

    private CreateCommentDto makeCreateCommentDto(Long id, String text, Long itemId, Long authorId) {

        CreateCommentDto dto = new CreateCommentDto();
        dto.setId(id);
        dto.setText(text);
        dto.setItemId(itemId);
        dto.setAuthorId(authorId);

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

    private Comment makeComment(Long id, String text, Item item, User author, LocalDateTime created) {

        Comment comment = new Comment();
        comment.setId(id);
        comment.setText(text);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(created);

        return comment;
    }
}
