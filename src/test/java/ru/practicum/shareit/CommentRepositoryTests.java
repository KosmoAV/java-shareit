package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.interfaces.CommentRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
public class CommentRepositoryTests {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    TestEntityManager testEntityManager;

    private User user;
    private Request request;
    private Item item;

    private Comment comment;

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

        comment = new Comment();
        comment.setText("Как же я устал писать тесты!!!");
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        testEntityManager.persist(comment);

        testEntityManager.flush();
    }

    @Test
    void findByItemIdTest() throws Exception {

        List<Comment> commentList = commentRepository.findByItemId(item.getId());
        assertThat(commentList, notNullValue());
        assertThat(commentList, hasSize(1));
        assertThat(commentList.get(0).getId(), notNullValue());
        assertThat(commentList.get(0).getText(), equalTo(comment.getText()));
        assertThat(commentList.get(0).getItem(), equalTo(item));
        assertThat(commentList.get(0).getAuthor(), equalTo(user));
        assertThat(commentList.get(0).getCreated(), notNullValue());
    }

    @Test
    void findByItemsIdTest() throws Exception {

        List<Comment> commentList = commentRepository.findByItemsId(List.of(item.getId(), 2L, 3L));
        assertThat(commentList, notNullValue());
        assertThat(commentList, hasSize(1));
        assertThat(commentList.get(0).getId(), notNullValue());
        assertThat(commentList.get(0).getText(), equalTo(comment.getText()));
        assertThat(commentList.get(0).getItem(), equalTo(item));
        assertThat(commentList.get(0).getAuthor(), equalTo(user));
        assertThat(commentList.get(0).getCreated(), notNullValue());
    }
}
