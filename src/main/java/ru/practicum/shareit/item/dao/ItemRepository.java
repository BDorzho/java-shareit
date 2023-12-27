package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import javax.transaction.Transactional;
import java.util.List;


public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findItemsByOwnerId(Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Item i WHERE i.owner.id = :userId AND i.id = :itemId")
    void deleteByUserIdAndItemId(@Param("userId") Long userId, @Param("itemId") Long itemId);

    @Modifying
    @Transactional
    @Query("UPDATE Item i SET i.name = :name, i.description = :description, i.available = :available WHERE i.id = :itemId AND i.owner.id = :userId")
    void update(@Param("userId") Long userId,
                @Param("itemId") Long itemId,
                @Param("name") String name,
                @Param("description") String description,
                @Param("available") Boolean available);

    @Query("SELECT i FROM Item i " +
            "WHERE (UPPER(i.name) LIKE UPPER(CONCAT('%', :searchText, '%')) " +
            "OR UPPER(i.description) LIKE UPPER(CONCAT('%', :searchText, '%'))) " +
            "AND i.available = true")
    List<Item> search(@Param("searchText") String searchText);


}
