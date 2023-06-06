package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    // all
    List<Booking> findAllByBookerIdOrderByStartDesc(long bookerId);

    @Query("select b from Booking as b " +
            "where b.item.owner.id = ?1 " +
            "order by b.start desc")
    List<Booking> findAllByOwnerId(long ownerId);

    // current
    @Query("select b from Booking as b " +
            "where b.booker.id = ?1 " +
            "and current_timestamp between b.start and b.end " +
            "order by b.start desc")
    List<Booking> findAllByBookerIdAndStateCurrent(long bookerId);

    @Query("select b from Booking as b " +
            "where b.item.owner.id = ?1 " +
            "and current_timestamp between b.start and b.end " +
            "order by b.start desc")
    List<Booking> findAllByOwnerIdAndStateCurrent(long ownerId);

    // past
    @Query("select b from Booking as b " +
            "where b.booker.id = ?1 " +
            "and current_timestamp > b.end " +
            "order by b.start desc")
    List<Booking> findAllByBookerIdAndStatePast(long bookerId);

    @Query("select b from Booking as b " +
            "where b.item.owner.id = ?1 " +
            "and current_timestamp > b.end " +
            "order by b.start desc")
    List<Booking> findAllByOwnerIdAndStatePast(long ownerId);

    // future
    @Query("select b from Booking as b " +
            "where b.booker.id = ?1 " +
            "and current_timestamp < b.start " +
            "order by b.start desc")
    List<Booking> findAllByBookerIdAndStateFuture(long bookerId);

    @Query("select b from Booking as b " +
            "where b.item.owner.id = ?1 " +
            "and current_timestamp < b.start " +
            "order by b.start desc")
    List<Booking> findAllByOwnerIdAndStateFuture(long ownerId);

    // waiting
    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long bookerId, Status status);

    @Query("select b from Booking as b " +
            "where b.item.owner.id = ?1 " +
            "and b.status = ?2 " +
            "order by b.start desc")
    List<Booking> findAllByOwnerIdAndStatus(long ownerId, Status status);

    Optional<Booking> findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(long itemId,
                                                                             LocalDateTime time,
                                                                             Status status);

    Optional<Booking> findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(long itemId,
                                                                             LocalDateTime time,
                                                                             Status status);

    boolean existsByBookerIdAndItemIdAndEndBefore(long bookerId, long itemId, LocalDateTime end);
}
