package ru.practicum.shareit.booking.dao;

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
    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId")
    List<Booking> findBookingByItemId(@Param("itemId") Long itemId);

    boolean existsByItemIdAndEndAfterAndStartBefore(Long itemId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT b, i FROM Booking b " +
            " JOIN b.item i " +
            " WHERE b.booker.id = :bookerId ORDER BY b.start DESC")
    List<Booking> findBookingsByBookerId(@Param("bookerId") Long bookerId);

    @Query("SELECT b, i FROM Booking b" +
            " JOIN b.item i " +
            "WHERE b.booker.id = :bookerId AND b.end < :end ORDER BY b.end DESC")
    List<Booking> findBookingsByBookerIdAndEndDateBefore(@Param("bookerId") Long bookerId,
                                                         @Param("end") LocalDateTime end);

    @Query("SELECT b, i FROM Booking b" +
            " JOIN b.item i " +
            "WHERE b.booker.id = :bookerId AND b.start > :now ORDER BY b.start DESC")
    List<Booking> findBookingsByBookerIdAndStartDateAfter(@Param("bookerId") Long bookerId,
                                                          @Param("now") LocalDateTime now);

    @Query("SELECT b, i FROM Booking b" +
            " JOIN b.item i " +
            "WHERE b.booker.id = :bookerId AND b.status = :status ORDER BY b.start DESC")
    List<Booking> findBookingsByBookerIdAndStatus(@Param("bookerId") Long bookerId,
                                                  @Param("status") BookingStatus status);

    @Query("SELECT b, i FROM Booking b " +
            "JOIN b.item i " +
            "WHERE b.booker.id = :bookerId AND :time BETWEEN b.start AND b.end ORDER BY b.start ASC")
    List<Booking> findBookingsByBookerIdAndStartBeforeAndEndAfter(@Param("bookerId") Long bookerId,
                                                                  @Param("time") LocalDateTime time);

    @Query("SELECT b, i FROM Booking b " +
            " JOIN b.item i " +
            " WHERE i.owner.id = :ownerId ORDER BY b.start DESC")
    List<Booking> findBookingsByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT b, i FROM Booking b" +
            " JOIN b.item i " +
            "WHERE i.owner.id = :ownerId AND b.end < :end ORDER BY b.end DESC")
    List<Booking> findBookingsByOwnerIdAndEndDateBefore(@Param("ownerId") Long ownerId,
                                                        @Param("end") LocalDateTime end);

    @Query("SELECT b, i FROM Booking b" +
            " JOIN b.item i " +
            "WHERE i.owner.id = :ownerId AND b.start > :now ORDER BY b.start DESC")
    List<Booking> findBookingsByOwnerIdAndStartDateAfter(@Param("ownerId") Long ownerId,
                                                         @Param("now") LocalDateTime now);

    @Query("SELECT b, i FROM Booking b" +
            " JOIN b.item i " +
            "WHERE i.owner.id = :ownerId AND b.status = :status ORDER BY b.start DESC")
    List<Booking> findBookingsByOwnerIdAndStatus(@Param("ownerId") Long ownerId,
                                                 @Param("status") BookingStatus status);

    @Query("SELECT b, i FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.owner.id = :ownerId AND :time BETWEEN b.start AND b.end ORDER BY b.start ASC")
    List<Booking> findBookingsByOwnerIdAndStartBeforeAndEndAfter(@Param("ownerId") Long ownerId,
                                                                 @Param("time") LocalDateTime time);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.booker.id = :bookerId AND b.end < :now")
    List<Booking> findBookingByItemIdAndBookerIdAndEndTimeBeforeNow(@Param("itemId") Long itemId,
                                                                    @Param("bookerId") Long bookerId,
                                                                    @Param("now") LocalDateTime now);
}
