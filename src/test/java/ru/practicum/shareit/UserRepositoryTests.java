package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.interfaces.UserRepository;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    TestEntityManager testEntityManager;

    private User user;

    @BeforeEach
    void init() {

        user = new User();
        user.setName("Alex");
        user.setEmail("Kosmo@poza.com");
    }

    @Test
    void saveUserTest() throws Exception {

        User saveUser = userRepository.save(user);
        assertThat(saveUser, notNullValue());
        assertThat(saveUser.getId(), notNullValue());
        assertThat(saveUser.getName(), equalTo(user.getName()));
        assertThat(saveUser.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void findByIdUserTest() throws Exception {

        testEntityManager.persist(user);

        User saveUser = userRepository.findById(user.getId()).orElse(new User());
        assertThat(saveUser, notNullValue());
        assertThat(saveUser.getId(), notNullValue());
        assertThat(saveUser.getName(), equalTo(user.getName()));
        assertThat(saveUser.getEmail(), equalTo(user.getEmail()));
    }
}
