package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest()
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIntegrationTests {

    private final EntityManager entityManager;
    private final BookingService bookingService;

    @BeforeEach
    public void init() {
        UserDto ownerDto = makeUserDto("Owner", "Alex@mail.ru");
        User owner = UserMapper.toUser(ownerDto);
        entityManager.persist(owner);
        entityManager.flush();

        TypedQuery<User> userQuery = entityManager.createQuery("Select u from User u", User.class);
        long ownerId = userQuery.getSingleResult().getId();

        ItemDto itemDto = makeItemDto(null, "Item", "Description", true,
                ownerId, null);
        Item item = ItemMapper.toItem(itemDto, null);
        entityManager.persist(item);
        entityManager.flush();

        UserDto userDto = makeUserDto("User", "Ivan@yandex.ru");
        User user = UserMapper.toUser(userDto);
        entityManager.persist(user);
        entityManager.flush();
    }

    @Test
    public void addBookingTest() {

        TypedQuery<Item> itemQuery = entityManager.createQuery("Select i from Item i", Item.class);
        long itemId = itemQuery.getSingleResult().getId();

        TypedQuery<User> userQuery = entityManager.createQuery("Select u from User u Where u.name = :name", User.class);
        long userId = userQuery.setParameter("name", "User").getSingleResult().getId();

        CreateBookingDto createBookingDto = makeCreateBookingDto(itemId, userId);
        ResponseBookingDto responseBookingDto = bookingService.addBooking(createBookingDto);

        assertThat(responseBookingDto.getId(), notNullValue());
        assertThat(responseBookingDto.getStart(), equalTo(createBookingDto.getStart()));
        assertThat(responseBookingDto.getEnd(), equalTo(createBookingDto.getEnd()));
        assertThat(responseBookingDto.getItem().getId(), equalTo(createBookingDto.getItemId()));
        assertThat(responseBookingDto.getBooker().getId(), equalTo(createBookingDto.getBookerId()));
        assertThat(responseBookingDto.getStatus(), equalTo(createBookingDto.getStatus()));
    }

    @Test
    public void approveBookingTest() {

        TypedQuery<Item> itemQuery = entityManager.createQuery("Select i from Item i", Item.class);
        long itemId = itemQuery.getSingleResult().getId();

        TypedQuery<User> userQuery = entityManager.createQuery("Select u from User u Where u.name = :name", User.class);
        long ownerId = userQuery.setParameter("name", "Owner").getSingleResult().getId();

        userQuery = entityManager.createQuery("Select u from User u Where u.name = :name", User.class);
        long userId = userQuery.setParameter("name", "User").getSingleResult().getId();

        CreateBookingDto createBookingDto = makeCreateBookingDto(itemId, userId);
        long bookingId = bookingService.addBooking(createBookingDto).getId();

        ResponseBookingDto responseBookingDto = bookingService.approveBooking(ownerId, bookingId, true);

        assertThat(responseBookingDto.getId(), notNullValue());
        assertThat(responseBookingDto.getStart(), equalTo(createBookingDto.getStart()));
        assertThat(responseBookingDto.getEnd(), equalTo(createBookingDto.getEnd()));
        assertThat(responseBookingDto.getItem().getId(), equalTo(createBookingDto.getItemId()));
        assertThat(responseBookingDto.getBooker().getId(), equalTo(createBookingDto.getBookerId()));
        assertThat(responseBookingDto.getStatus(), equalTo(Status.APPROVED));
    }

    @Test
    public void getBookingTest() {

        TypedQuery<Item> itemQuery = entityManager.createQuery("Select i from Item i", Item.class);
        long itemId = itemQuery.getSingleResult().getId();

        TypedQuery<User> userQuery = entityManager.createQuery("Select u from User u Where u.name = :name", User.class);
        long userId = userQuery.setParameter("name", "User").getSingleResult().getId();

        CreateBookingDto createBookingDto = makeCreateBookingDto(itemId, userId);
        long bookingId = bookingService.addBooking(createBookingDto).getId();

        ResponseBookingDto responseBookingDto = bookingService.getBooking(userId, bookingId);

        assertThat(responseBookingDto.getId(), notNullValue());
        assertThat(responseBookingDto.getStart(), equalTo(createBookingDto.getStart()));
        assertThat(responseBookingDto.getEnd(), equalTo(createBookingDto.getEnd()));
        assertThat(responseBookingDto.getItem().getId(), equalTo(createBookingDto.getItemId()));
        assertThat(responseBookingDto.getBooker().getId(), equalTo(createBookingDto.getBookerId()));
        assertThat(responseBookingDto.getStatus(), equalTo(createBookingDto.getStatus()));
    }

    @Test
    public void getAllBookingTest() {

        TypedQuery<Item> itemQuery = entityManager.createQuery("Select i from Item i", Item.class);
        long itemId = itemQuery.getSingleResult().getId();

        TypedQuery<User> userQuery = entityManager.createQuery("Select u from User u Where u.name = :name", User.class);
        long userId = userQuery.setParameter("name", "User").getSingleResult().getId();

        CreateBookingDto createBookingDto = makeCreateBookingDto(itemId, userId);
        long bookingId = bookingService.addBooking(createBookingDto).getId();

        List<ResponseBookingDto> responseBookingDtoList = bookingService.getAllBooking(userId,
                "WAITING", null, null);

        assertThat(responseBookingDtoList.size(), equalTo(1));

        ResponseBookingDto responseBookingDto = responseBookingDtoList.get(0);

        assertThat(responseBookingDto.getId(), notNullValue());
        assertThat(responseBookingDto.getStart(), equalTo(createBookingDto.getStart()));
        assertThat(responseBookingDto.getEnd(), equalTo(createBookingDto.getEnd()));
        assertThat(responseBookingDto.getItem().getId(), equalTo(createBookingDto.getItemId()));
        assertThat(responseBookingDto.getBooker().getId(), equalTo(createBookingDto.getBookerId()));
        assertThat(responseBookingDto.getStatus(), equalTo(createBookingDto.getStatus()));

        responseBookingDtoList = bookingService.getAllBooking(userId,
                "PAST", null, null);

        assertThat(responseBookingDtoList.size(), equalTo(1));

        responseBookingDto = responseBookingDtoList.get(0);

        assertThat(responseBookingDto.getId(), notNullValue());
        assertThat(responseBookingDto.getStart(), equalTo(createBookingDto.getStart()));
        assertThat(responseBookingDto.getEnd(), equalTo(createBookingDto.getEnd()));
        assertThat(responseBookingDto.getItem().getId(), equalTo(createBookingDto.getItemId()));
        assertThat(responseBookingDto.getBooker().getId(), equalTo(createBookingDto.getBookerId()));
        assertThat(responseBookingDto.getStatus(), equalTo(createBookingDto.getStatus()));
    }

    @Test
    public void getAllOwnerBookingTest() {

        TypedQuery<Item> itemQuery = entityManager.createQuery("Select i from Item i", Item.class);
        long itemId = itemQuery.getSingleResult().getId();

        TypedQuery<User> userQuery = entityManager.createQuery("Select u from User u Where u.name = :name", User.class);
        long userId = userQuery.setParameter("name", "User").getSingleResult().getId();

        CreateBookingDto createBookingDto = makeCreateBookingDto(itemId, userId);
        long bookingId = bookingService.addBooking(createBookingDto).getId();

        userQuery = entityManager.createQuery("Select u from User u Where u.name = :name", User.class);
        long ownerId = userQuery.setParameter("name", "Owner").getSingleResult().getId();

        List<ResponseBookingDto> responseBookingDtoList = bookingService.getAllOwnerBooking(ownerId,
                "WAITING", null, null);

        assertThat(responseBookingDtoList.size(), equalTo(1));

        ResponseBookingDto responseBookingDto = responseBookingDtoList.get(0);

        assertThat(responseBookingDto.getId(), notNullValue());
        assertThat(responseBookingDto.getStart(), equalTo(createBookingDto.getStart()));
        assertThat(responseBookingDto.getEnd(), equalTo(createBookingDto.getEnd()));
        assertThat(responseBookingDto.getItem().getId(), equalTo(createBookingDto.getItemId()));
        assertThat(responseBookingDto.getBooker().getId(), equalTo(createBookingDto.getBookerId()));
        assertThat(responseBookingDto.getStatus(), equalTo(createBookingDto.getStatus()));

        responseBookingDtoList = bookingService.getAllOwnerBooking(ownerId,
                "PAST", null, null);

        assertThat(responseBookingDtoList.size(), equalTo(1));

        responseBookingDto = responseBookingDtoList.get(0);

        assertThat(responseBookingDto.getId(), notNullValue());
        assertThat(responseBookingDto.getStart(), equalTo(createBookingDto.getStart()));
        assertThat(responseBookingDto.getEnd(), equalTo(createBookingDto.getEnd()));
        assertThat(responseBookingDto.getItem().getId(), equalTo(createBookingDto.getItemId()));
        assertThat(responseBookingDto.getBooker().getId(), equalTo(createBookingDto.getBookerId()));
        assertThat(responseBookingDto.getStatus(), equalTo(createBookingDto.getStatus()));
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

    private CreateBookingDto makeCreateBookingDto(Long itemId, Long bookerId) {

        CreateBookingDto dto = new CreateBookingDto();
        dto.setId(null);
        dto.setStart(LocalDateTime.now().minusDays(1));
        dto.setEnd(LocalDateTime.now().minusHours(10));
        dto.setItemId(itemId);
        dto.setBookerId(bookerId);
        dto.setStatus(Status.WAITING);

        return dto;
    }
}
