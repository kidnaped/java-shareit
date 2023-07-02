package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    // all
    List<Booking> findAllByBookerId(long bookerId, Pageable pageable);

    @Query("select b from Booking as b " +
            "where b.item.owner.id = ?1 " +
            "order by b.start desc")
    List<Booking> findAllByOwnerId(long ownerId, Pageable pageable);

    // current
    @Query("select b from Booking as b " +
            "where b.booker.id = ?1 " +
            "and current_timestamp between b.start and b.end " +
            "order by b.start desc")
    List<Booking> findAllByBookerIdAndStateCurrent(long bookerId, Pageable pageable);

    @Query("select b from Booking as b " +
            "where b.item.owner.id = ?1 " +
            "and current_timestamp between b.start and b.end " +
            "order by b.start desc")
    List<Booking> findAllByOwnerIdAndStateCurrent(long ownerId, Pageable pageable);

    // past
    @Query("select b from Booking as b " +
            "where b.booker.id = ?1 " +
            "and current_timestamp > b.end " +
            "order by b.start desc")
    List<Booking> findAllByBookerIdAndStatePast(long bookerId, Pageable pageable);

    @Query("select b from Booking as b " +
            "where b.item.owner.id = ?1 " +
            "and current_timestamp > b.end " +
            "order by b.start desc")
    List<Booking> findAllByOwnerIdAndStatePast(long ownerId, Pageable pageable);

    // future
    @Query("select b from Booking as b " +
            "where b.booker.id = ?1 " +
            "and current_timestamp < b.start " +
            "order by b.start desc")
    List<Booking> findAllByBookerIdAndStateFuture(long bookerId, Pageable pageable);

    @Query("select b from Booking as b " +
            "where b.item.owner.id = ?1 " +
            "and current_timestamp < b.start " +
            "order by b.start desc")
    List<Booking> findAllByOwnerIdAndStateFuture(long ownerId, Pageable pageable);

    // waiting
    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long bookerId, Status status, Pageable pageable);

    @Query("select b from Booking as b " +
            "where b.item.owner.id = ?1 " +
            "and b.status = ?2 " +
            "order by b.start desc")
    List<Booking> findAllByOwnerIdAndStatus(long ownerId, Status status, Pageable pageable);

    Optional<Booking> findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(long itemId,
                                                                             LocalDateTime time,
                                                                             Status status);

    Optional<Booking> findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(long itemId,
                                                                             LocalDateTime time,
                                                                             Status status);

    boolean existsByBookerIdAndItemIdAndEndBefore(long bookerId, long itemId, LocalDateTime end);
}
