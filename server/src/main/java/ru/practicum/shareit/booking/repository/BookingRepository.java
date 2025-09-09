package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b from Booking as b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.user u " +
            "JOIN FETCH b.booker bo " +
            "WHERE bo.id = :userId " +
            "order by b.start DESC")
    List<Booking> findBookingContainStateAll(@Param("userId") Long userId);

    @Query("select b from Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.user u " +
            "JOIN FETCH b.booker bo " +
            "where b.start < :localDateTime " +
            "and b.end > :localDateTime " +
            "and b.status = :status " +
            "and bo.id = :userId " +
            "order by b.start DESC")
    List<Booking> findBookingContainStateCurrent(@Param("localDateTime") LocalDateTime localDateTime,
                                                 @Param("status") Status status, @Param("userId") Long userId);

    @Query("select b from Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.user u " +
            "JOIN FETCH b.booker bo " +
            "where b.start < :localDateTime " +
            "and b.end < :localDateTime " +
            "and b.status = :status " +
            "and bo.id = :userId " +
            "order by b.start DESC")
    List<Booking> findBookingContainStatePast(@Param("localDateTime") LocalDateTime localDateTime,
                                              @Param("status") Status status, @Param("userId") Long userId);

    @Query("select b from Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.user u " +
            "JOIN FETCH b.booker bo " +
            "where b.start > :localDateTime " +
            "and b.status = :status " +
            "and bo.id = :userId " +
            "order by b.start DESC")
    List<Booking> findBookingContainStateFutureAndWaiting(@Param("localDateTime") LocalDateTime localDateTime,
                                                          @Param("status") Status status, @Param("userId") Long userId);

    @Query("select b from Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.user u " +
            "JOIN FETCH b.booker bo " +
            "WHERE b.status = :status " +
            "and bo.id = :userId " +
            "order by b.start DESC")
    List<Booking> findBookingContainStateRejected(@Param("status") Status status, @Param("userId") Long userId);

    @Query("select b from Booking as b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.user u " +
            "JOIN FETCH b.booker " +
            "WHERE i.user.id = :userId " +
            "order by b.start DESC")
    List<Booking> findBookingOwnerItemWithStateAll(@Param("userId") Long userId);

    @Query("select b from Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.user u " +
            "JOIN FETCH b.booker " +
            "where b.start < :localDateTime " +
            "and b.end > :localDateTime " +
            "and b.status = :status " +
            "and i.user.id = :userId " +
            "order by b.start DESC")
    List<Booking> findBookingOwnerItemWithStateCurrent(@Param("localDateTime") LocalDateTime localDateTime,
                                                       @Param("status") Status status, @Param("userId") Long userId);


    @Query("select b from Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.user u " +
            "JOIN FETCH b.booker " +
            "where b.start < :localDateTime " +
            "and b.end < :localDateTime " +
            "and b.status = :status " +
            "and i.user.id = :userId " +
            "order by b.start DESC")
    List<Booking> findBookingOwnerItemWithStatePast(@Param("localDateTime") LocalDateTime localDateTime,
                                                    @Param("status") Status status, @Param("userId") Long userId);

    @Query("select b from Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH b.booker " +
            "JOIN FETCH i.user u " +
            "where b.start > :localDateTime " +
            "and b.status = :status " +
            "and i.user.id = :userId " +
            "order by b.start DESC")
    List<Booking> findBookingOwnerItemWithStateFutureAndWaiting(@Param("localDateTime") LocalDateTime localDateTime,
                                                                @Param("status") Status status, @Param("userId") Long userId);

    @Query("select b from Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH b.booker " +
            "JOIN FETCH i.user u " +
            "WHERE b.status = :status " +
            "and i.user.id = :userId " +
            "order by b.start DESC")
    List<Booking> findBookingOwnerItemWithStateRejected(@Param("status") Status status, @Param("userId") Long userId);


    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.item i " +
            "WHERE i.id IN :itemIds " +
            "AND ( (b.status = 'APPROVED' AND b.end < :now) " +
            "OR ( (b.status = 'WAITING' OR b.status = 'APPROVED') AND b.start > :now ) )")
    List<Booking> findAllByItemIds(@Param("itemIds") List<Long> itemIds, @Param("now") LocalDateTime now);

    @Query("select b from Booking b " +
            "WHERE b.booker.id = :userId " +
            "AND b.status = 'APPROVED' " +
            "AND b.end < :now " +
            "AND b.item.id = :itemId")
    List<Booking> findBookingForComment(@Param("userId") Long userId,
                                        @Param("itemId") Long itemId,
                                        @Param("now") LocalDateTime now);

    @Query("select b from Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.user u " +
            "JOIN FETCH b.booker boo " +
            "WHERE u.id = :userId " +
            "AND b.id = :bookingId")
    Optional<Booking> findBookingByIdAndItemUserId(@Param("userId") Long userId, @Param("bookingId") Long bookingId);

    @Query("select b from Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.user u " +
            "JOIN FETCH b.booker boo " +
            "WHERE (u.id = :userId " +
            "OR boo.id = :userId) " +
            "AND b.id = :bookingId")
    Optional<Booking> findBookingByIdAndBookerIdAndItemUserId(@Param("userId") Long userId,
                                                              @Param("bookingId") Long bookingId);

    @Query("select b from Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.user u " +
            "JOIN FETCH b.booker boo " +
            "WHERE b.id = :bookingId")
    Optional<Booking> findBookingById(@Param("bookingId") Long bookingId);
}
