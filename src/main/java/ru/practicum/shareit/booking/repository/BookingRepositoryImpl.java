package ru.practicum.shareit.booking.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.helpers.Constant.BookingStatus;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class BookingRepositoryImpl implements CustomBookingRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Booking> findFirstByItemIdsNextBooking(@Param("itemIds") List<Long> itemIds,
                                                       @Param("status") BookingStatus status,
                                                       LocalDateTime time) {
        String sql = "SELECT DISTINCT b FROM Booking b WHERE b.item.id IN ?1 AND " +
                "b.start > ?2 AND b.status = ?3 ORDER BY b.start DESC";
        return entityManager.createQuery(sql, Booking.class)
                .setParameter(1, itemIds)
                .setParameter(2, time)
                .setParameter(3, status)
                .getResultList();
    }

    @Override
    public List<Booking> findFirstByItemIdsLastBooking(@Param("itemIds") List<Long> itemIds,
                                                       @Param("status") BookingStatus status,
                                                       LocalDateTime time) {
        String sql = "SELECT DISTINCT b FROM Booking b WHERE b.item.id IN ?1 AND " +
                "b.start < ?2 AND b.status = ?3 ORDER BY b.start DESC";
        return entityManager.createQuery(sql, Booking.class)
                .setParameter(1, itemIds)
                .setParameter(2, time)
                .setParameter(3, status)
                .getResultList();
    }
}
