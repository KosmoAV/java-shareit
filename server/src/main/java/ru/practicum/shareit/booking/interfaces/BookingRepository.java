package ru.practicum.shareit.booking.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    public Page<Booking> findByBookerIdOrderByStartDesc(long bookerId, PageRequest page);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.booker.id = ?1 AND " +
            "b.start < CURRENT_TIMESTAMP AND b.end > CURRENT_TIMESTAMP ORDER BY b.start DESC")
    public Page<Booking> findByBookerIdWithCurrentState(long bookerId, PageRequest page);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.booker.id = ?1 AND " +
            "b.end < CURRENT_TIMESTAMP ORDER BY b.start DESC")
    public Page<Booking> findByBookerIdWithPastState(long bookerId, PageRequest page);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.booker.id = ?1 AND " +
            "b.start > CURRENT_TIMESTAMP ORDER BY b.start DESC")
    public Page<Booking> findByBookerIdWithFutureState(long bookerId, PageRequest page);

    public Page<Booking> findByBookerIdAndStatusOrderByStartDesc(long bookerId, Status status, PageRequest page);

    public Page<Booking> findByItemOwnerIdOrderByStartDesc(long ownerId, PageRequest page);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.item.ownerId = ?1 AND " +
            "b.start < CURRENT_TIMESTAMP AND b.end > CURRENT_TIMESTAMP ORDER BY b.start DESC")
    public Page<Booking> findByItemOwnerIdWithCurrentState(long ownerId, PageRequest page);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.item.ownerId = ?1 AND " +
            "b.end < CURRENT_TIMESTAMP ORDER BY b.start DESC")
    public Page<Booking> findByItemOwnerIdWithPastState(long ownerId, PageRequest page);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.item.ownerId = ?1 AND " +
            "b.start > CURRENT_TIMESTAMP ORDER BY b.start DESC")
    public Page<Booking> findByItemOwnerIdWithFutureState(long ownerId, PageRequest page);

    public Page<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(long ownerId, Status status, PageRequest page);

    @Query(value = "SELECT * FROM bookings WHERE (item_id = ?1 AND " +
            "start_date < CURRENT_TIMESTAMP AND status = ?2) ORDER BY start_date DESC LIMIT 1;", nativeQuery = true)
    public Booking findLastBookingItem(long itemId, String status);

    @Query(value = "SELECT * FROM bookings WHERE (item_id = ?1 AND " +
            "start_date > CURRENT_TIMESTAMP AND status = ?2) ORDER BY start_date LIMIT 1;", nativeQuery = true)
    public Booking findNextBookingItem(long itemId, String status);

    public boolean existsByItemIdAndBookerIdAndEndBefore(long itemId, long bookerId, LocalDateTime now);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.item.id IN (?1) AND b.status = ?2 ORDER BY b.start")
    public List<Booking> findByItemIdAndStatusOrderByStart(List<Long> itemId, Status status);
}
