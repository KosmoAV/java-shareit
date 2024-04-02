package ru.practicum.shareit.itemtests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
public class ItemRepositoryTests {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    TestEntityManager testEntityManager;

    private User user;
    private Request request;
    private Item item;

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

        item = new Item();
        item.setName("Pencil");
        item.setDescription("Very small");
        item.setAvailable(true);
        item.setOwnerId(user.getId());
        item.setRequest(request);
        testEntityManager.persist(item);

        testEntityManager.flush();
    }

    @Test
    void findByOwnerIdTest() throws Exception {

        List<Item> itemList = itemRepository.findByOwnerId(user.getId());
        assertThat(itemList, notNullValue());
        assertThat(itemList, hasSize(1));
        assertThat(itemList.get(0).getId(), notNullValue());
        assertThat(itemList.get(0).getName(), equalTo(item.getName()));
        assertThat(itemList.get(0).getDescription(), equalTo(item.getDescription()));
        assertThat(itemList.get(0).getAvailable(), equalTo(item.getAvailable()));
        assertThat(itemList.get(0).getOwnerId(), equalTo(item.getOwnerId()));
        assertThat(itemList.get(0).getRequest(), notNullValue());
        assertThat(itemList.get(0).getRequest().getId(), equalTo(item.getRequest().getId()));
    }

    @Test
    void findByNameOrDescriptionContainingIgnoreCaseAndAvailableTrueTest() throws Exception {

        List<Item> itemList = itemRepository.findByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue("sma", "sma");
        assertThat(itemList, notNullValue());
        assertThat(itemList, hasSize(1));
        assertThat(itemList.get(0).getId(), notNullValue());
        assertThat(itemList.get(0).getName(), equalTo(item.getName()));
        assertThat(itemList.get(0).getDescription(), equalTo(item.getDescription()));
        assertThat(itemList.get(0).getAvailable(), equalTo(item.getAvailable()));
        assertThat(itemList.get(0).getOwnerId(), equalTo(item.getOwnerId()));
        assertThat(itemList.get(0).getRequest(), notNullValue());
        assertThat(itemList.get(0).getRequest().getId(), equalTo(item.getRequest().getId()));
    }

    @Test
    void findByRequestIdsTest() throws Exception {

        List<Item> itemList = itemRepository.findByRequestIds(List.of(request.getId(), 2L, 3L));
        assertThat(itemList, notNullValue());
        assertThat(itemList, hasSize(1));
        assertThat(itemList.get(0).getId(), notNullValue());
        assertThat(itemList.get(0).getName(), equalTo(item.getName()));
        assertThat(itemList.get(0).getDescription(), equalTo(item.getDescription()));
        assertThat(itemList.get(0).getAvailable(), equalTo(item.getAvailable()));
        assertThat(itemList.get(0).getOwnerId(), equalTo(item.getOwnerId()));
        assertThat(itemList.get(0).getRequest(), notNullValue());
        assertThat(itemList.get(0).getRequest().getId(), equalTo(item.getRequest().getId()));
    }
}
