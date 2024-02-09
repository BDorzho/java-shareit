package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findItemsByOwnerId(long userId, Pageable pageable);

    @Modifying
    @Transactional
    void deleteByIdAndOwnerId(Long itemId, long ownerId);

    @Query("SELECT i FROM Item i " +
            "WHERE (UPPER(i.name) LIKE UPPER(CONCAT('%', :searchText, '%')) " +
            "OR UPPER(i.description) LIKE UPPER(CONCAT('%', :searchText, '%'))) " +
            "AND i.available = true")
    List<Item> search(@Param("searchText") String searchText, Pageable pageable);


    List<Item> findByRequestIdIn(List<Long> requestIds);

    List<Item> findByRequestId(long requestId);
}
