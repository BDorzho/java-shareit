package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

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
                                                             @Param("bookerId") Long bookerId,
                                                             @Param("now") LocalDateTime now);

    @EntityGraph(value = "booking-with-item-and-booker")
    List<Booking> findByBooker_Id(Long bookerId, Sort sort);

    @EntityGraph(value = "booking-with-item-and-booker")
    List<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);

    @EntityGraph(value = "booking-with-item-and-booker")
    List<Booking> findByBooker_IdAndStartIsAfter(Long bookerId,
                                                 LocalDateTime now, Sort sort);

    @EntityGraph(value = "booking-with-item-and-booker")
    List<Booking> findByBooker_IdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    @EntityGraph(value = "booking-with-item-and-booker")
    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime time, LocalDateTime now, Sort sort);

    @EntityGraph(value = "booking-with-item-and-booker")
    List<Booking> findByItem_Owner_Id(Long ownerId, Sort sort);

    @EntityGraph(value = "booking-with-item-and-booker")
    List<Booking> findByItem_Owner_IdAndEndIsBefore(Long ownerId, LocalDateTime end, Sort sort);

    @EntityGraph(value = "booking-with-item-and-booker")
    List<Booking> findByItem_Owner_IdAndStartIsAfter(Long ownerId, LocalDateTime now, Sort sort);

    @EntityGraph(value = "booking-with-item-and-booker")
    List<Booking> findByItem_Owner_IdAndStatus(Long ownerId, BookingStatus status, Sort sort);

    @EntityGraph(value = "booking-with-item-and-booker")
    List<Booking> findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(Long ownerId, LocalDateTime time, LocalDateTime now, Sort sort);


}
