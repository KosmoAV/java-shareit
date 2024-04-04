package ru.practicum.shareit.requesttests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.interfaces.RequestRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;
import java.util.List;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
public class RequestRepositoryTests {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    TestEntityManager testEntityManager;

    private User user;
    private Request request;

    @BeforeEach
    void init() {

        user = new User();
        user.setName("Alex");
        user.setEmail("Kosmo@poza.com");
        testEntityManager.persist(user);

        request = new Request();
        request.setDescription("Description");
        request.setCreated(LocalDateTime.now());
        request.setRequestor(user);
        testEntityManager.persist(request);

        testEntityManager.flush();
    }

    @Test
    void findByRequestorIdTest() throws Exception {

        assertThat(user.getId(), notNullValue());
        List<Request> requestList = requestRepository.findByRequestorId(user.getId());
        assertThat(requestList, notNullValue());
        assertThat(requestList, hasSize(1));
        assertThat(requestList.get(0).getId(), notNullValue());
        assertThat(requestList.get(0).getDescription(), equalTo(request.getDescription()));
        assertThat(requestList.get(0).getCreated(), equalTo(request.getCreated()));
        assertThat(requestList.get(0).getRequestor(), equalTo(user));
    }

    @Test
    void findByOtherRequestorIdTest() throws Exception {

        List<Request> requestList = requestRepository.findByOtherRequestorId(user.getId() + 1);
        assertThat(requestList, notNullValue());
        assertThat(requestList, hasSize(1));
        assertThat(requestList.get(0).getId(), notNullValue());
        assertThat(requestList.get(0).getDescription(), equalTo(request.getDescription()));
        assertThat(requestList.get(0).getCreated(), equalTo(request.getCreated()));
        assertThat(requestList.get(0).getRequestor(), equalTo(user));
    }

    @Test
    void findByOtherRequestorIdPageTest() throws Exception {

        PageRequest page = PageRequest.of(0, 32);
        List<Request> requestList = requestRepository.findByOtherRequestorId(user.getId() + 1, page).toList();
        assertThat(requestList, notNullValue());
        assertThat(requestList, hasSize(1));
        assertThat(requestList.get(0).getId(), notNullValue());
        assertThat(requestList.get(0).getDescription(), equalTo(request.getDescription()));
        assertThat(requestList.get(0).getCreated(), equalTo(request.getCreated()));
        assertThat(requestList.get(0).getRequestor(), equalTo(user));
    }
}
