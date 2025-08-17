package ru.practicum.shareit.item.repository;

import jakarta.validation.constraints.Positive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c " +
            "JOIN FETCH c.item i " +
            "JOIN FETCH i.user u " +
            "WHERE i.id = :itemId")
    List<Comment> findCommentsByItemId(@Param("itemId") Long itemId);

    @Query("select c from Comment c " +
            "JOIN FETCH c.item i " +
            "JOIN FETCH i.user u " +
            "WHERE i.id IN :itemId")
    List<Comment> findCommentsOwner(@Param("itemId") List itemId);
}
