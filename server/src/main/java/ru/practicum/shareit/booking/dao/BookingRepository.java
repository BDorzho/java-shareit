package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @EntityGraph(value = "booking-with-item-and-booker")
    @Query("SELECT b FROM Booking b WHERE b.item.id IN :itemIds")
    List<Booking> findByItems(List<Long> itemIds);

    @EntityGraph(value = "booking-with-item-and-booker")
    List<Booking> findByItemId(Long itemId);

    boolean existsByItemIdAndEndAfterAndStartBefore(Long itemId, LocalDateTime startDate, LocalDateTime endDate);

    @EntityGraph(value = "booking-with-item-and-booker")
    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.booker.id = :bookerId AND b.end < :now")
    List<Booking> findByItemIdAndBookerIdAndEndTimeBeforeNow(@Param("itemId") Long itemId,
                                                             @Param("bookerId") long bookerId,
                                                             @Param("now") LocalDateTime now);

    @EntityGraph(value = "booking-with-item-and-booker")
    List<Booking> findByBooker_Id(long bookerId, Pageable pageable);

    @EntityGraph(value = "booking-with-item-and-booker")
    List<Booking> findByBooker_IdAndEndIsBefore(long bookerId, LocalDateTime end, Pageable pageable);

    @EntityGraph(value = "booking-with-item-and-booker")
    List<Booking> findByBooker_IdAndStartIsAfter(long bookerId,
                                                 LocalDateTime now, Pageable pageable);

    @EntityGraph(value = "booking-with-item-and-booker")
    List<Booking> findByBooker_IdAndStatus(long bookerId, BookingStatus status, Pageable pageable);

    @EntityGraph(value = "booking-with-item-and-booker")
    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfter(long bookerId, LocalDateTime time, LocalDateTime now, Pageable pageable);

    @EntityGraph(value = "booking-with-item-and-booker")
    List<Booking> findByItem_Owner_Id(long ownerId, Pageable pageable);

    @EntityGraph(value = "booking-with-item-and-booker")
    List<Booking> findByItem_Owner_IdAndEndIsBefore(long ownerId, LocalDateTime end, Pageable pageable);

    @EntityGraph(value = "booking-with-item-and-booker")
    List<Booking> findByItem_Owner_IdAndStartIsAfter(long ownerId, LocalDateTime now, Pageable pageable);

    @EntityGraph(value = "booking-with-item-and-booker")
    List<Booking> findByItem_Owner_IdAndStatus(long ownerId, BookingStatus status, Pageable pageable);

    @EntityGraph(value = "booking-with-item-and-booker")
    List<Booking> findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(long ownerId, LocalDateTime time, LocalDateTime now, Pageable pageable);


}
