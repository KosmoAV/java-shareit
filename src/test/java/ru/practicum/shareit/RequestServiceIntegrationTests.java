package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.ResponseRequestDto;
import ru.practicum.shareit.request.interfaces.RequestService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest()
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceIntegrationTests {

    private final EntityManager entityManager;
    private final RequestService requestService;

    @Test
    public void addRequestTest() {

        UserDto userDto = makeUserDto("Alex", "Alex@mail.ru");

        User entity = UserMapper.toUser(userDto);
        entityManager.persist(entity);
        entityManager.flush();

        TypedQuery<User> query = entityManager.createQuery("Select u from User u", User.class);
        long userId = query.getSingleResult().getId();

        CreateRequestDto createRequestDto = makeCreateRequestDto("Description done");
        ResponseRequestDto responseRequestDto = requestService.addRequest(userId, createRequestDto);

        assertThat(responseRequestDto.getId(), notNullValue());
        assertThat(responseRequestDto.getDescription(), equalTo(createRequestDto.getDescription()));
        assertThat(responseRequestDto.getCreated(), notNullValue());
        assertThat(responseRequestDto.getItems(), empty());
    }

    @Test
    public void getUserRequestsTest() {

        UserDto userDto = makeUserDto("Alex", "Alex@mail.ru");

        User entity = UserMapper.toUser(userDto);
        entityManager.persist(entity);
        entityManager.flush();

        CreateRequestDto createRequestDto = makeCreateRequestDto("Description done");

        TypedQuery<User> query = entityManager.createQuery("Select u from User u", User.class);
        long userId = query.getSingleResult().getId();

        requestService.addRequest(userId, createRequestDto);

        List<ResponseRequestDto> responseRequestDtoList = requestService.getUserRequests(userId);

        assertThat(responseRequestDtoList, hasItem(allOf(
                hasProperty("id", notNullValue()),
                hasProperty("description", equalTo(createRequestDto.getDescription())),
                hasProperty("created", notNullValue()),
                hasProperty("items", empty())
        )));
    }

    @Test
    public void getAllRequestsTest() {

        UserDto userDto = makeUserDto("Alex", "Alex@mail.ru");

        User entity = UserMapper.toUser(userDto);
        entityManager.persist(entity);
        entityManager.flush();

        CreateRequestDto createRequestDto = makeCreateRequestDto("Description done");

        TypedQuery<User> query = entityManager.createQuery("Select u from User u", User.class);
        long userId = query.getSingleResult().getId();

        requestService.addRequest(userId, createRequestDto);

        List<ResponseRequestDto> responseRequestDtoList = requestService.getAllRequests(userId + 1, null, null);

        assertThat(responseRequestDtoList, hasItem(allOf(
                hasProperty("id", notNullValue()),
                hasProperty("description", equalTo(createRequestDto.getDescription())),
                hasProperty("created", notNullValue()),
                hasProperty("items", empty())
        )));
    }

    @Test
    public void getRequestByIdTest() {

        UserDto userDto = makeUserDto("Alex", "Alex@mail.ru");

        User entity = UserMapper.toUser(userDto);
        entityManager.persist(entity);
        entityManager.flush();

        TypedQuery<User> query = entityManager.createQuery("Select u from User u", User.class);
        long userId = query.getSingleResult().getId();

        CreateRequestDto createRequestDto = makeCreateRequestDto("Description done");
        long requestId = requestService.addRequest(userId, createRequestDto).getId();

        ResponseRequestDto responseRequestDto = requestService.getRequestById(userId, requestId);

        assertThat(responseRequestDto.getId(), notNullValue());
        assertThat(responseRequestDto.getDescription(), equalTo(createRequestDto.getDescription()));
        assertThat(responseRequestDto.getCreated(), notNullValue());
        assertThat(responseRequestDto.getItems(), empty());
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

}
