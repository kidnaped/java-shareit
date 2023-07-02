package ru.practicum.shareit.item;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoTest {
    @Autowired
    private JacksonTester<CommentDto> commentDtoJacksonTester;

    @Test
    @SneakyThrows
    void commentDtoSerializationTest() {
        User author = User.builder()
                .id(1L)
                .name("Author")
                .email("author@ya.ru")
                .build();

        User owner = User.builder()
                .id(2L)
                .name("Owner")
                .email("owner@ya.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("Iron")
                .description("Iron iron")
                .owner(owner)
                .available(true)
                .build();

        Comment comment = Comment.builder()
                .id(1L)
                .text("Nice!")
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .item(item)
                .author(author)
                .build();

        CommentDto commentDto = CommentMapper.toDto(comment);
        JsonContent<CommentDto> commentDtoJsonContent = commentDtoJacksonTester.write(commentDto);

        assertThat(commentDtoJsonContent).extractingJsonPathNumberValue("$.id")
                .isEqualTo(Math.toIntExact(comment.getId()));
        assertThat(commentDtoJsonContent).extractingJsonPathStringValue("$.text")
                .isEqualTo(comment.getText());
        assertThat(commentDtoJsonContent).extractingJsonPathStringValue("$.authorName")
                .isEqualTo(comment.getAuthor().getName());
        assertThat(commentDtoJsonContent).extractingJsonPathStringValue("$.created")
                .isEqualTo(comment.getCreated().toString());
    }
}