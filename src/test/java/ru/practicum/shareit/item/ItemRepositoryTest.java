package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ItemRepositoryTest {
    private static final Pageable PAGEABLE = PageRequest.of(0, 10);

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void beforeEach() {
        User user1 = new User();
        user1.setEmail("test1@test.com");
        user1.setName("test1");

        Item item1 = new Item();
        item1.setName("test1");
        item1.setDescription("description");
        item1.setUser(user1);
        item1.setAvailable(true);

        User user2 = new User();
        user2.setEmail("test2@test.com");
        user2.setName("test2");

        Item item2 = new Item();
        item2.setName("test2");
        item2.setDescription("descriptionTest");
        item2.setUser(user1);
        item2.setAvailable(true);

        entityManager.persist(user1);
        entityManager.persist(item1);

        entityManager.persist(user2);
        entityManager.persist(item2);
    }

    @Test
    public void findAllByUserIdTest() {
        List<Item> result = itemRepository.findAllByUserId(1L, PAGEABLE);

        assertThat(result.size(), is(2));
        assertThat(result.get(0).getId(), is(1L));
    }

    @Test
    public void findAllByContainsTextTest() {
        List<Item> result = itemRepository.findAllByContainsText("descriptiontest", PAGEABLE);

        assertThat(result.size(), is(1));
        assertThat(result.get(0).getId(), is(2L));
    }
}
