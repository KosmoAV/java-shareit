package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseCommentDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.interfaces.ItemService;
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
public class ItemServiceIntegrationTests {

    private final EntityManager entityManager;
    private final ItemService itemService;

    @Test
    public void addItemTest() {

        UserDto userDto = makeUserDto("Alex", "Alex@mail.ru");

        User entity = UserMapper.toUser(userDto);
        entityManager.persist(entity);
        entityManager.flush();

        TypedQuery<User> query = entityManager.createQuery("Select u from User u", User.class);
        long userId = query.getSingleResult().getId();

        ItemDto itemDto = makeItemDto(null, "Item", "Description", true,
                userId, null);
        ItemDto saveItemDto = itemService.addItem(itemDto);

        assertThat(saveItemDto.getId(), notNullValue());
        assertThat(saveItemDto.getName(), equalTo(itemDto.getName()));
        assertThat(saveItemDto.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(saveItemDto.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(saveItemDto.getOwner(), equalTo(itemDto.getOwner()));
        assertThat(saveItemDto.getRequestId(), equalTo(itemDto.getRequestId()));
    }

    @Test
    public void updateItemTest() {

        UserDto userDto = makeUserDto("Alex", "Alex@mail.ru");

        User entity = UserMapper.toUser(userDto);
        entityManager.persist(entity);
        entityManager.flush();

        TypedQuery<User> query = entityManager.createQuery("Select u from User u", User.class);
        long userId = query.getSingleResult().getId();

        ItemDto itemDto = makeItemDto(null, "Item", "Description", true,
                userId, null);
        ItemDto saveItemDto = itemService.addItem(itemDto);
        saveItemDto.setDescription("New description");

        ItemDto updateItemDto = itemService.updateItem(saveItemDto);

        assertThat(updateItemDto.getId(), equalTo(saveItemDto.getId()));
        assertThat(updateItemDto.getName(), equalTo(saveItemDto.getName()));
        assertThat(updateItemDto.getDescription(), equalTo(saveItemDto.getDescription()));
        assertThat(updateItemDto.getAvailable(), equalTo(saveItemDto.getAvailable()));
        assertThat(updateItemDto.getOwner(), equalTo(saveItemDto.getOwner()));
        assertThat(updateItemDto.getRequestId(), equalTo(saveItemDto.getRequestId()));
    }

    @Test
    public void getItemTest() {

        UserDto userDto = makeUserDto("Alex", "Alex@mail.ru");

        User entity = UserMapper.toUser(userDto);
        entityManager.persist(entity);
        entityManager.flush();

        TypedQuery<User> query = entityManager.createQuery("Select u from User u", User.class);
        long userId = query.getSingleResult().getId();

        ItemDto itemDto = makeItemDto(null, "Item", "Description", true,
                userId, null);
        long itemId = itemService.addItem(itemDto).getId();

        ResponseItemDto saveItemDto = itemService.getItem(userId, itemId);

        assertThat(saveItemDto.getId(), notNullValue());
        assertThat(saveItemDto.getName(), equalTo(itemDto.getName()));
        assertThat(saveItemDto.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(saveItemDto.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(saveItemDto.getOwner(), equalTo(itemDto.getOwner()));
        assertThat(saveItemDto.getRequest(), equalTo(itemDto.getRequestId()));
        assertThat(saveItemDto.getLastBooking(), nullValue());
        assertThat(saveItemDto.getNextBooking(), nullValue());
        assertThat(saveItemDto.getComments(), empty());
    }

    @Test
    public void getItemsTest() {

        UserDto userDto = makeUserDto("Alex", "Alex@mail.ru");

        User entity = UserMapper.toUser(userDto);
        entityManager.persist(entity);
        entityManager.flush();

        TypedQuery<User> query = entityManager.createQuery("Select u from User u", User.class);
        long userId = query.getSingleResult().getId();

        ItemDto itemDto = makeItemDto(null, "Item", "Description", true,
                userId, null);
        itemService.addItem(itemDto);

        List<ResponseItemDto> saveItemDtoList = itemService.getItems(userId);

        assertThat(saveItemDtoList, hasItem(allOf(
                hasProperty("id", notNullValue()),
                hasProperty("name", equalTo(itemDto.getName())),
                hasProperty("description", equalTo(itemDto.getDescription())),
                hasProperty("available", equalTo(itemDto.getAvailable())),
                hasProperty("owner", equalTo(itemDto.getOwner())),
                hasProperty("request", equalTo(itemDto.getRequestId())),
                hasProperty("lastBooking", nullValue()),
                hasProperty("nextBooking", nullValue()),
                hasProperty("comments", empty())
        )));
    }

    @Test
    public void searchItemsTest() {

        UserDto userDto = makeUserDto("Alex", "Alex@mail.ru");

        User entity = UserMapper.toUser(userDto);
        entityManager.persist(entity);
        entityManager.flush();

        TypedQuery<User> query = entityManager.createQuery("Select u from User u", User.class);
        long userId = query.getSingleResult().getId();

        ItemDto itemDto = makeItemDto(null, "Item", "Description", true,
                userId, null);
        itemService.addItem(itemDto);

        List<ItemDto> saveItemDtoList = itemService.searchItems(userId, "ipt");

        assertThat(saveItemDtoList, hasItem(allOf(
                hasProperty("id", notNullValue()),
                hasProperty("name", equalTo(itemDto.getName())),
                hasProperty("description", equalTo(itemDto.getDescription())),
                hasProperty("available", equalTo(itemDto.getAvailable())),
                hasProperty("owner", equalTo(itemDto.getOwner())),
                hasProperty("requestId", equalTo(itemDto.getRequestId()))
        )));
    }

    @Test
    public void addCommentTest() {

        UserDto ownerDto = makeUserDto("Alex", "Alex@mail.ru");
        User entity = UserMapper.toUser(ownerDto);
        entityManager.persist(entity);
        entityManager.flush();

        TypedQuery<User> query = entityManager.createQuery("Select u from User u", User.class);
        long ownerId = query.getSingleResult().getId();

        ItemDto itemDto = makeItemDto(null, "Item", "Description", true,
                ownerId, null);
        ItemDto saveItemDto = itemService.addItem(itemDto);

        UserDto userDto = makeUserDto("Ivan", "Ivan@yandex.ru");
        entity = UserMapper.toUser(userDto);
        entityManager.persist(entity);
        entityManager.flush();

        query = entityManager.createQuery("Select u from User u Where u.name = :name", User.class);
        User user = query.setParameter("name", userDto.getName()).getSingleResult();

        CreateBookingDto createBookingDto = makeCreateBookingDto(saveItemDto.getId(), user.getId());
        Booking booking = BookingMapper.toBooking(createBookingDto, ItemMapper.toItem(saveItemDto, null), user);
        entityManager.persist(booking);
        entityManager.flush();

        CreateCommentDto createCommentDto = makeCreateCommentDto(null, "Comment", saveItemDto.getId(), user.getId());

        ResponseCommentDto responseCommentDto = itemService.addComment(createCommentDto);

        assertThat(responseCommentDto.getId(), notNullValue());
        assertThat(responseCommentDto.getText(), equalTo(createCommentDto.getText()));
        assertThat(responseCommentDto.getAuthorName(), equalTo(userDto.getName()));
        assertThat(responseCommentDto.getCreated(), notNullValue());
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
        dto.setStatus(Status.APPROVED);

        return dto;
    }
}
