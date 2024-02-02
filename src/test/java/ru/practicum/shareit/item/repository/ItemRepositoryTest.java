package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.persistence.TypedQuery;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    TestEntityManager em;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    public void testFindItemsByOwnerId() {
        // given
        User owner = new User();
        owner.setName("Test User");
        owner.setEmail("test@email.com");
        userRepository.save(owner);


        Item item = new Item();
        item.setName("Item 1");
        item.setDescription("Description Item 1");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item);

        // when
        Page<Item> items = itemRepository.findItemsByOwnerId(owner.getId(), PageRequest.of(0, 10));

        // then
        assertThat(items).isNotEmpty();
        assertThat(items.getTotalElements()).isEqualTo(1);
    }

    @Test
    public void testDeleteByIdAndOwnerId() {
        // given
        User owner = new User();
        owner.setName("Test User");
        owner.setEmail("test@email.com");
        userRepository.save(owner);


        Item item = new Item();
        item.setName("Item 1");
        item.setDescription("Description Item 1");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item);

        // when
        itemRepository.deleteByIdAndOwnerId(item.getId(), owner.getId());

        // then
        assertThat(itemRepository.findById(item.getId())).isEmpty();
    }

    @Test
    public void testSearch() {
        // given
        User owner = new User();
        owner.setName("Test User");
        owner.setEmail("test@email.com");
        userRepository.save(owner);


        Item item = new Item();
        item.setName("Item 1");
        item.setDescription("Description Item 1");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item);

        // when
        Page<Item> items = itemRepository.search("item", PageRequest.of(0, 10));

        // then
        TypedQuery<Item> query =
                em.getEntityManager().createQuery("SELECT i FROM Item i " +
                        "WHERE (UPPER(i.name) LIKE UPPER(CONCAT('%', :searchText, '%')) " +
                        "OR UPPER(i.description) LIKE UPPER(CONCAT('%', :searchText, '%'))) " +
                        "AND i.available = true", Item.class);
        query.setParameter("searchText", "item");
        Item findItem = query.getSingleResult();

        assertThat(findItem).isNotNull();
        assertThat(items.getTotalElements()).isEqualTo(1);
    }
}
