package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query("select ir from ItemRequest ir " +
            "JOIN FETCH ir.user u " +
            "WHERE u.id = :userId")
    List<ItemRequest> findItemRequestByUserId(@Param("userId") Long userId);

    @Query("select ir from ItemRequest ir " +
            "JOIN FETCH ir.user u " +
            "LEFT JOIN FETCH ir.item i " +
            "ORDER BY ir.id DESC")
    List<ItemRequest> findItemRequestOrderByIdDESC();

    @Query("select ir from ItemRequest ir " +
            "JOIN FETCH ir.user " +
            "LEFT JOIN FETCH ir.item " +
            "WHERE ir.id = :requestId")
    ItemRequest findItemRequest(@Param("requestId") Long requestId);

}
