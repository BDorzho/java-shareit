package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface CommentRepository extends JpaRepository<Comment, Long> {
    @EntityGraph(value = "comment-with-author")
    List<Comment> findByItemId(Long itemId);

    @EntityGraph(value = "comment-with-author")
    @Query("SELECT c FROM Comment c WHERE c.item.id IN :itemIds")
    List<Comment> findByItems(List<Long> itemIds);
}
