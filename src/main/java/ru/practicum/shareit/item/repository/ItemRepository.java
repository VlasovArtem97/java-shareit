package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.dto.ItemDtoSearch;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select i from Item i " +
            "where (upper(i.name) like upper(concat('%', ?1, '%')) " +
            "or upper(i.description) like upper(concat('%', ?1, '%'))) " +
            "and i.available = true")
    List<ItemDtoSearch> search(String text);

    @Query("select i from Item i " +
            "JOIN FETCH i.user " +
            "WHERE i.id = :itemId")
    Optional<Item> findItemByIdWithUser(@Param("itemId") Long itemId);

    List<Item> findItemByUser_Id(Long userId);


}
