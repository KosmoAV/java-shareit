package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.ResponseCommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {
    public static ResponseCommentDto toResponseCommentDto(Comment comment) {

        if (comment == null) {
            throw new IllegalArgumentException("Parameter comment in method toResponseCommentDto must be non-null");
        }

        ResponseCommentDto responseCommentDto = new ResponseCommentDto();

        responseCommentDto.setId(comment.getId());
        responseCommentDto.setText(comment.getText());
        responseCommentDto.setAuthorName(comment.getAuthor().getName());
        responseCommentDto.setCreated(comment.getCreated());

        return responseCommentDto;
    }

    public static List<ResponseCommentDto> toResponseCommentDto(List<Comment> comments) {

        if (comments == null) {
            throw new IllegalArgumentException("Parameter comments in method toResponseCommentDto must be non-null");
        }

        return comments.stream()
                .map(CommentMapper::toResponseCommentDto)
                .collect(Collectors.toList());
    }

    public static Comment toComment(CreateCommentDto createCommentDto, Item item, User author) {

        if (createCommentDto == null) {
            throw new IllegalArgumentException("Parameter createCommentDto in method toComment must be non-null");
        }

        Comment comment = new Comment();

        comment.setId(createCommentDto.getId());
        comment.setText(createCommentDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        return comment;
    }
}
