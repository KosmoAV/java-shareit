package ru.practicum.shareit.booking.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    public List<Booking> findByBookerIdOrderByStartDesc(long bookerId);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.booker.id = ?1 AND " +
            "b.start < CURRENT_TIMESTAMP AND b.end > CURRENT_TIMESTAMP ORDER BY b.start")
    public List<Booking> findByBookerIdWithCurrentState(long bookerId);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.booker.id = ?1 AND " +
            "b.end < CURRENT_TIMESTAMP ORDER BY b.start DESC")
    public List<Booking> findByBookerIdWithPastState(long bookerId);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.booker.id = ?1 AND " +
            "b.start > CURRENT_TIMESTAMP ORDER BY b.start DESC")
    public List<Booking> findByBookerIdWithFutureState(long bookerId);

    public List<Booking> findByBookerIdAndStatusOrderByStartDesc(long bookerId, Status status);

    public List<Booking> findByItemOwnerIdOrderByStartDesc(long ownerId);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.item.ownerId = ?1 AND " +
            "b.start < CURRENT_TIMESTAMP AND b.end > CURRENT_TIMESTAMP ORDER BY b.start DESC")
    public List<Booking> findByItemOwnerIdWithCurrentState(long ownerId);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.item.ownerId = ?1 AND " +
            "b.end < CURRENT_TIMESTAMP ORDER BY b.start DESC")
    public List<Booking> findByItemOwnerIdWithPastState(long ownerId);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.item.ownerId = ?1 AND " +
            "b.start > CURRENT_TIMESTAMP ORDER BY b.start DESC")
    public List<Booking> findByItemOwnerIdWithFutureState(long ownerId);

    public List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(long ownerId, Status status);

    @Query(value = "SELECT * FROM bookings WHERE (item_id = ?1 AND " +
            "start_date < CURRENT_TIMESTAMP AND status = ?2) ORDER BY start_date DESC LIMIT 1;", nativeQuery = true)
    public Booking findLastBookingItem(long itemId, int status);

    @Query(value = "SELECT * FROM bookings WHERE (item_id = ?1 AND " +
            "start_date > CURRENT_TIMESTAMP AND status = ?2) ORDER BY start_date LIMIT 1;", nativeQuery = true)
    public Booking findNextBookingItem(long itemId, int status);

    public boolean existsByItemIdAndBookerIdAndEndBefore(long itemId, long bookerId, LocalDateTime now);
}
