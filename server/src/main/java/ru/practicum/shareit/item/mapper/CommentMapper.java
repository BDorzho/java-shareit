package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommentMapper {
    public static CommentDto toDto(Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }

    public static Comment toModel(CommentDto commentDto, Item item, User author) {
        return new Comment(commentDto.getId(),
                item,
                author,
                commentDto.getText(),
                LocalDateTime.now());
    }

    public static List<CommentDto> toListDto(List<Comment> commentList) {
        return commentList.stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
    }
}
