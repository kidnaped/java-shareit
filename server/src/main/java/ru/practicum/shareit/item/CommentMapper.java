package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {
    public static CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .itemId(comment.getItem().getId())
                .created(comment.getCreated())
                .build();
    }

    public static List<CommentDto> toDto(Iterable<Comment> comments) {
        List<CommentDto> dtos = new ArrayList<>();
        comments.forEach(comment -> dtos.add(toDto(comment)));
        return dtos;
    }

    public static Comment fromDto(CommentDto dto, Comment comment) {
        comment.setText(dto.getText());
        comment.setCreated(LocalDateTime.now());
        return comment;
    }
}
