package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerOrderByIdAsc(User user, Pageable pageable);

    List<Item> findAllByRequestIdIn(List<Long> ids);

    List<Item> findAllByRequestId(long requestId);

    @Query("select i from Item as i " +
            "where lower(i.name) like lower(concat('%', ?1, '%')) " +
            "or lower(i.description) like lower(concat('%', ?1, '%'))")
    List<Item> search(String text, Pageable pageable);
}