package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.helpers.Constant.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long>, CustomBookingRepository {
    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId ORDER BY b.start DESC")
    List<Booking> findAllBookingById(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.status = :status ORDER BY b.start DESC")
    List<Booking> findAllBookingByIdAndStatus(@Param("userId") Long userId, @Param("status") BookingStatus status,
                                              Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.end < CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findAllPastBookingById(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.start > CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findAllFutureBookingById(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND " +
        "(b.start < CURRENT_TIMESTAMP AND b.end > CURRENT_TIMESTAMP) ORDER BY b.start DESC")
    List<Booking> findAllCurrentBookingById(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.user.id = :userId ORDER BY b.start DESC")
    List<Booking> findAllBookingOwnerById(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.user.id = :userId AND b.status = :status ORDER BY b.start DESC")
    List<Booking> findAllBookingOwnerByIdAndStatus(@Param("userId") Long userId, @Param("status") BookingStatus status,
                                                   Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.user.id = :userId AND b.end < CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findAllPastOwnerBookingById(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.user.id = :userId AND b.start > CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findAllFutureOwnerBookingById(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.user.id = :userId AND " +
            "(b.start < CURRENT_TIMESTAMP AND b.end > CURRENT_TIMESTAMP) ORDER BY b.start DESC")
    List<Booking> findAllCurrentOwnerBookingById(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND " +
            "b.end < :time ORDER BY b.start DESC")
    List<Booking> findFirstByItemIdCurrentBooking(@Param("itemId") Long itemId,
                                               LocalDateTime time,
                                               Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND " +
            "b.start < :time AND b.status = :status ORDER BY b.start DESC")
    List<Booking> findFirstByItemIdLastBooking(@Param("itemId") Long itemId,
                                               @Param("status") BookingStatus status,
                                               LocalDateTime time,
                                               Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND " +
            "b.start > :time AND b.status = :status ORDER BY b.start ASC")
    List<Booking> findFirstByItemIdNextBooking(@Param("itemId") Long itemId,
                                               @Param("status") BookingStatus status,
                                               LocalDateTime time,
                                                Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.item.id = :itemId " +
            "AND b.end < CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findBookingByIdUserAndIdItem(@Param("userId") Long userId, @Param("itemId") Long itemId);
}
