package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select i from Item i " +
            "where (upper(i.name) like upper(concat('%', ?1, '%')) " +
            "or upper(i.description) like upper(concat('%', ?1, '%'))) " +
            "and i.available = true")
    List<Item> search(String text);

    @Query("select i from Item i " +
            "JOIN FETCH i.user " +
            "WHERE i.id = :itemId")
    Optional<Item> findItemByIdWithUser(@Param("itemId") Long itemId);

    @Query("select i from Item i " +
            "JOIN FETCH i.user u " +
            "WHERE u.id = :userId")
    List<Item> findItemByUser_Id(@Param("userId") Long userId);

}
